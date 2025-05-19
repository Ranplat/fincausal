package com.semanticweb.model;

import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 句子模型
 * 表示一个句子，包含句子文本和解析结果
 */
public class Sentence {
    private String text; // 句子文本
    private int index; // 句子在文档中的索引
    private List<String> tokens; // 分词结果
    private List<String> lemmas; // 词元
    private List<String> posTags; // 词性标注
    private List<String> nerTags; // 命名实体识别标注
    private Tree parseTree; // 句法分析树
    private SemanticGraph dependencyParse; // 依存句法分析图
    
    /**
     * 构造函数
     */
    public Sentence() {
        this.tokens = new ArrayList<>();
        this.lemmas = new ArrayList<>();
        this.posTags = new ArrayList<>();
        this.nerTags = new ArrayList<>();
    }
    
    /**
     * 构造函数
     * 
     * @param text 句子文本
     */
    public Sentence(String text) {
        this.text = text;
        this.tokens = new ArrayList<>();
        this.lemmas = new ArrayList<>();
        this.posTags = new ArrayList<>();
        this.nerTags = new ArrayList<>();
    }
    
    /**
     * 构造函数
     * 
     * @param text 句子文本
     * @param index 句子索引
     */
    public Sentence(String text, int index) {
        this.text = text;
        this.index = index;
        this.tokens = new ArrayList<>();
        this.lemmas = new ArrayList<>();
        this.posTags = new ArrayList<>();
        this.nerTags = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public int getIndex() {
        return index;
    }
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public List<String> getTokens() {
        return tokens;
    }
    
    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }
    
    public void addToken(String token) {
        this.tokens.add(token);
    }
    
    public List<String> getLemmas() {
        return lemmas;
    }
    
    public void setLemmas(List<String> lemmas) {
        this.lemmas = lemmas;
    }
    
    public void addLemma(String lemma) {
        this.lemmas.add(lemma);
    }
    
    public List<String> getPosTags() {
        return posTags;
    }
    
    public void setPosTags(List<String> posTags) {
        this.posTags = posTags;
    }
    
    public void addPosTag(String posTag) {
        this.posTags.add(posTag);
    }
    
    public List<String> getNerTags() {
        return nerTags;
    }
    
    public void setNerTags(List<String> nerTags) {
        this.nerTags = nerTags;
    }
    
    public void addNerTag(String nerTag) {
        this.nerTags.add(nerTag);
    }
    
    public Tree getParseTree() {
        return parseTree;
    }
    
    public void setParseTree(Tree parseTree) {
        this.parseTree = parseTree;
    }
    
    public SemanticGraph getDependencyParse() {
        return dependencyParse;
    }
    
    public void setDependencyParse(SemanticGraph dependencyParse) {
        this.dependencyParse = dependencyParse;
    }
    
    @Override
    public String toString() {
        return String.format("Sentence{index=%d, text='%s', tokens=%d}", index, text, tokens.size());
    }
}