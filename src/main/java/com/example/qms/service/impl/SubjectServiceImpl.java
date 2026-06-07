package com.example.qms.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.qms.dto.SubjectCreateDto;
import com.example.qms.entity.Score;
import com.example.qms.entity.Student;
import com.example.qms.entity.Subject;
import com.example.qms.mapper.ScoreMapper;
import com.example.qms.mapper.StudentMapper;
import com.example.qms.mapper.SubjectMapper;
import com.example.qms.service.SubjectService;
import com.example.qms.util.CsvUtil;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger log = LoggerFactory.getLogger(SubjectServiceImpl.class);

    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private ScoreMapper scoreMapper;
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public List<Subject> findByTeacherId(Integer teacherId) {
        return subjectMapper.findByTeacherId(teacherId);
    }

    @Override
    public Subject findById(Integer id) {
        return subjectMapper.findById(id);
    }

    @Override
    public void create(SubjectCreateDto dto, Integer teacherId) {
        Subject subject = new Subject();
        subject.setTeacherId(teacherId);
        subject.setName(dto.getName());
        subject.setSemester(dto.getSemester());
        subject.setRobotWebhook(dto.getRobotWebhook());
        subjectMapper.insert(subject);
    }

    @Override
    public void delete(Integer id) {
        subjectMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Map<String, Object> uploadScores(Integer subjectId, MultipartFile file) throws IOException {
        Subject subject = subjectMapper.findById(subjectId);
        if (subject == null) {
            throw new RuntimeException("科目不存在");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new RuntimeException("文件名为空");
        }

        if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            return uploadScoresFromExcel(subjectId, file);
        } else {
            return uploadScoresFromCsv(subjectId, file);
        }
    }

    /**
     * 从 Excel 文件上传成绩（自动检测数据起始行）
     */
    private Map<String, Object> uploadScoresFromExcel(Integer subjectId, MultipartFile file) throws IOException {
        List<String[]> dataRows = new ArrayList<>();
        try {
            EasyExcel.read(file.getInputStream(), new AnalysisEventListener<Map<Integer, String>>() {
                private boolean dataStarted = false;
                private int rowIndex = 0;

                @Override
                public void invoke(Map<Integer, String> data, AnalysisContext context) {
                    rowIndex++;
                    String firstCell = data.get(0);
                    if (firstCell == null) firstCell = "";
                    firstCell = firstCell.trim();

                    // 检测表头行（包含“学号”关键词）
                    if (!dataStarted && "学号".equals(firstCell)) {
                        dataStarted = true;
                        log.info("检测到表头行，行号：{}", rowIndex);
                        return;
                    }

                    // 如果数据已开始，且当前行学号不为空，则作为数据行
                    if (dataStarted && !"学号".equals(firstCell) && !firstCell.isEmpty()) {
                        String studentId = firstCell;
                        String studentName = data.get(1) != null ? data.get(1).trim() : "";
                        String scoreValue = data.get(2) != null ? data.get(2).trim() : "";
                        if (studentId.isEmpty() || scoreValue.isEmpty()) {
                            log.warn("第{}行数据不完整：学号={}, 成绩={}", rowIndex, studentId, scoreValue);
                            return;
                        }
                        dataRows.add(new String[]{studentId, studentName, scoreValue});
                        log.info("读取数据行[{}]: 学号={}, 姓名={}, 成绩={}", rowIndex, studentId, studentName, scoreValue);
                    } else if (!dataStarted && !"学号".equals(firstCell) && !firstCell.isEmpty()) {
                        // 跳过标题行（如《课程名》等）
                        log.debug("跳过标题行：{}", rowIndex);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    log.info("Excel解析完成，有效数据行数：{}", dataRows.size());
                }
            }).sheet().doRead();
        } catch (Exception e) {
            log.error("Excel解析异常", e);
            throw new RuntimeException("Excel解析失败: " + e.getMessage(), e);
        }

        if (dataRows.isEmpty()) {
            throw new RuntimeException("未读取到有效数据，请确保表格包含学号、姓名、成绩列，且表头包含“学号”关键字");
        }
        return processScoreData(subjectId, dataRows);
    }

    /**
     * 从 CSV 文件上传成绩（原逻辑）
     */
    private Map<String, Object> uploadScoresFromCsv(Integer subjectId, MultipartFile file) throws IOException {
        try {
            List<String[]> rows = CsvUtil.parseCsv(file);
            if (rows.size() < 2) throw new RuntimeException("CSV文件无数据");
            List<String[]> dataRows = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 3) continue;
                dataRows.add(new String[]{row[0].trim(), row[1].trim(), row[2].trim()});
            }
            if (dataRows.isEmpty()) throw new RuntimeException("CSV文件无有效数据");
            return processScoreData(subjectId, dataRows);
        } catch (CsvException e) {
            throw new RuntimeException("CSV解析失败", e);
        }
    }

    /**
     * 处理成绩数据（公共逻辑：创建/更新学生，插入/更新成绩，返回统计）
     */
    private Map<String, Object> processScoreData(Integer subjectId, List<String[]> dataRows) {
        List<Double> allScores = new ArrayList<>();
        for (String[] row : dataRows) {
            String studentId = row[0];
            String studentName = row[1];
            String scoreValue = row[2];

            Student existingStudent = studentMapper.findByStudentId(studentId);
            if (existingStudent == null) {
                Student newStudent = new Student();
                newStudent.setStudentId(studentId);
                newStudent.setName(studentName.isEmpty() ? studentId : studentName);
                newStudent.setClassName("未分配");
                newStudent.setGrade("未分配");
                int insertResult = studentMapper.insert(newStudent);
                log.info("插入学生 {} 影响行数: {}", studentId, insertResult);
                if (insertResult != 1) throw new RuntimeException("插入学生失败");
            } else {
                if (!existingStudent.getName().equals(studentName) && !studentName.isEmpty()) {
                    existingStudent.setName(studentName);
                    studentMapper.updateById(existingStudent);
                    log.info("更新学生姓名 {}", studentId);
                }
            }

            Score existingScore = scoreMapper.findBySubjectIdAndStudentId(subjectId, studentId);
            if (existingScore == null) {
                Score score = new Score();
                score.setSubjectId(subjectId);
                score.setStudentId(studentId);
                score.setStudentName(studentName.isEmpty() ? studentId : studentName);
                score.setScore(scoreValue);
                scoreMapper.insert(score);
                log.info("插入成绩: 学号={}, 成绩={}", studentId, scoreValue);
            } else {
                existingScore.setScore(scoreValue);
                if (!studentName.isEmpty()) existingScore.setStudentName(studentName);
                scoreMapper.updateById(existingScore);
                log.info("更新成绩: 学号={}, 成绩={}", studentId, scoreValue);
            }

            try {
                allScores.add(Double.parseDouble(scoreValue));
            } catch (NumberFormatException e) {
                log.warn("非数字成绩: {}", scoreValue);
            }
        }

        Map<String, Object> stats = new HashMap<>();
        if (!allScores.isEmpty()) {
            double avg = allScores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
            long passCount = allScores.stream().filter(s -> s >= 60).count();
            long excellentCount = allScores.stream().filter(s -> s >= 90).count();
            stats.put("average", Math.round(avg * 100) / 100.0);
            stats.put("passRate", Math.round((double) passCount / allScores.size() * 100));
            stats.put("excellentRate", Math.round((double) excellentCount / allScores.size() * 100));
            stats.put("totalStudents", dataRows.size());
        }
        return stats;
    }

    @Override
    public List<Map<String, Object>> getScoresWithStudentInfo(Integer subjectId) {
        List<Score> scores = scoreMapper.findBySubjectId(subjectId);
        return scores.stream().map(score -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", score.getId());
            map.put("studentId", score.getStudentId());
            map.put("studentName", score.getStudentName());
            map.put("score", score.getScore());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateScore(Integer scoreId, String newScore) {
        Score score = scoreMapper.findById(scoreId);
        if (score != null) {
            score.setScore(newScore);
            scoreMapper.updateById(score);
        }
    }

    @Override
    public int countSubjects() {
        return subjectMapper.countSubjects();
    }

    @Override
    public void fillStudentCount(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) return;
        List<Integer> subjectIds = subjects.stream().map(Subject::getId).collect(Collectors.toList());
        List<Map<String, Object>> counts = subjectMapper.countStudentsBySubjectIds(subjectIds);
        Map<Integer, Integer> countMap = counts.stream().collect(Collectors.toMap(
                m -> (Integer) m.get("subject_id"),
                m -> ((Number) m.get("studentCount")).intValue()
        ));
        for (Subject subject : subjects) {
            subject.setStudentCount(countMap.getOrDefault(subject.getId(), 0));
        }
    }
}