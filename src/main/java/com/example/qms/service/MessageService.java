package com.example.qms.service;

import com.example.qms.entity.Message;
import java.util.List;

public interface MessageService {
    void sendMessage(Message message);
    void replyMessage(Integer messageId, String reply);
    List<Message> getMessagesByTeacher(Integer teacherId);
    List<Message> getMessagesByStudent(String studentId);
}