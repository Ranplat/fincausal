package com.fincausal.pipeline;

import com.fincausal.processor.*;

/**
 * 管道构建器
 * 使用构建器模式创建处理管道，支持灵活配置各个处理模块
 */
public class PipelineBuilder {
    private TextPreprocessor preprocessor;
    private NLPParser parser;
    private CausalRelationExtractor causalExtractor;
    private TemporalProcessor temporalProcessor;
    private FinancialDomainAdapter financialDomainAdapter;
    
    public PipelineBuilder() {
        // 默认构造函数
    }
    
    /**
     * 添加文本预处理器
     */
    public PipelineBuilder withPreprocessor() {
        this.preprocessor = new TextPreprocessor();
        return this;
    }
    
    /**
     * 添加自定义文本预处理器
     */
    public PipelineBuilder withPreprocessor(TextPreprocessor preprocessor) {
        this.preprocessor = preprocessor;
        return this;
    }
    
    /**
     * 添加NLP解析器
     */
    public PipelineBuilder withParser() {
        this.parser = new NLPParser();
        return this;
    }
    
    /**
     * 添加自定义NLP解析器
     */
    public PipelineBuilder withParser(NLPParser parser) {
        this.parser = parser;
        return this;
    }
    
    /**
     * 添加因果关系提取器
     */
    public PipelineBuilder withCausalExtractor() {
        this.causalExtractor = new CausalRelationExtractor();
        return this;
    }
    
    /**
     * 添加自定义因果关系提取器
     */
    public PipelineBuilder withCausalExtractor(CausalRelationExtractor extractor) {
        this.causalExtractor = extractor;
        return this;
    }
    
    /**
     * 添加时序处理器
     */
    public PipelineBuilder withTemporalProcessor() {
        this.temporalProcessor = new TemporalProcessor();
        return this;
    }
    
    /**
     * 添加自定义时序处理器
     */
    public PipelineBuilder withTemporalProcessor(TemporalProcessor processor) {
        this.temporalProcessor = processor;
        return this;
    }
    
    /**
     * 添加金融领域适配器
     */
    public PipelineBuilder withFinancialDomainAdapter() {
        this.financialDomainAdapter = new FinancialDomainAdapter();
        return this;
    }
    
    /**
     * 添加自定义金融领域适配器
     */
    public PipelineBuilder withFinancialDomainAdapter(FinancialDomainAdapter adapter) {
        this.financialDomainAdapter = adapter;
        return this;
    }
    
    /**
     * 构建处理管道
     */
    public Pipeline build() {
        return new DefaultPipeline(
                preprocessor,
                parser,
                causalExtractor,
                temporalProcessor,
                financialDomainAdapter
        );
    }
}