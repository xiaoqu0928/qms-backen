package com.example.qms.service.impl;

import com.example.qms.entity.Message;
import com.example.qms.mapper.MessageMapper;
import com.example.qms.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public void sendMessage(Message message) {
        message.setStatus(0);
        messageMapper.insert(message);
    }

    @Override
    @Transactional
    public void replyMessage(Integer messageId, String reply) {
        Message message = messageMapper.findById(messageId);  // 现在有 findById 方法
        if (message != null) {
            message.setReply(reply);
            message.setStatus(1);
            message.setRepliedAt(new Date());
            messageMapper.updateById(message);
        }
    }

    @Override
    public List<Message> getMessagesByTeacher(Integer teacherId) {
        return messageMapper.findByTeacherId(teacherId);
    }

    @Override
    public List<Message> getMessagesByStudent(String studentId) {
        return messageMapper.findByStudentId(studentId);
    }
}