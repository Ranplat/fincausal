package com.semanticweb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 因果三元组模型
 * 表示一个因果关系，包含原因、结果和关系类型
 */
public class CausalTriple {
    @JsonProperty("cause")
    private String cause; // 原因
    
    @JsonProperty("effect")
    private String effect; // 结果
    
    @JsonProperty("relation_type")
    private String relationType; // 关系类型
    
    @JsonProperty("confidence")
    private double confidence; // 置信度
    
    @JsonProperty("temporal_relation")
    private String temporalRelation; // 时序关系（before/after等）
    
    @JsonProperty("domain_category")
    private String domainCategory; // 领域分类（如金融领域分类）
    
    // 默认构造函数（用于Jackson反序列化）
    public CausalTriple() {
    }
    
    /**
     * 构造函数
     */
    public CausalTriple(String cause, String effect, String relationType) {
        this.cause = cause;
        this.effect = effect;
        this.relationType = relationType;
        this.confidence = 1.0;
    }
    
    /**
     * 构造函数（带置信度）
     */
    public CausalTriple(String cause, String effect, String relationType, double confidence) {
        this.cause = cause;
        this.effect = effect;
        this.relationType = relationType;
        this.confidence = confidence;
    }
    
    // Getters and Setters
    public String getCause() {
        return cause;
    }
    
    public void setCause(String cause) {
        this.cause = cause;
    }
    
    public String getEffect() {
        return effect;
    }
    
    public void setEffect(String effect) {
        this.effect = effect;
    }
    
    public String getRelationType() {
        return relationType;
    }
    
    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }
    
    public double getConfidence() {
        return confidence;
    }
    
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }
    
    public String getTemporalRelation() {
        return temporalRelation;
    }
    
    public void setTemporalRelation(String temporalRelation) {
        this.temporalRelation = temporalRelation;
    }
    
    public String getDomainCategory() {
        return domainCategory;
    }
    
    public void setDomainCategory(String domainCategory) {
        this.domainCategory = domainCategory;
    }
    
    @Override
    public String toString() {
        return String.format("CausalTriple{cause='%s', effect='%s', relationType='%s', confidence=%.2f, temporalRelation='%s', domainCategory='%s'}",
                cause, effect, relationType, confidence, temporalRelation, domainCategory);
    }
}