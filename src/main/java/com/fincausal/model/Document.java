package com.semanticweb.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档模型
 * 表示一个解析后的文档，包含文档文本和句子列表
 */
public class Document {
    private String text; // 文档文本
    private List<Sentence> sentences; // 句子列表
    
    /**
     * 构造函数
     */
    public Document() {
        this.sentences = new ArrayList<>();
    }
    
    /**
     * 构造函数
     * 
     * @param text 文档文本
     */
    public Document(String text) {
        this.text = text;
        this.sentences = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public List<Sentence> getSentences() {
        return sentences;
    }
    
    public void setSentences(List<Sentence> sentences) {
        this.sentences = sentences;
    }
    
    /**
     * 添加句子
     * 
     * @param sentence 句子
     */
    public void addSentence(Sentence sentence) {
        this.sentences.add(sentence);
    }
    
    /**
     * 获取句子数量
     * 
     * @return 句子数量
     */
    public int getSentenceCount() {
        return sentences.size();
    }
    
    @Override
    public String toString() {
        return String.format("Document{sentences=%d}", sentences.size());
    }
}