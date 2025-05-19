package com.semanticweb.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.semanticweb.model.CausalTriple;
import com.semanticweb.model.Document;
import com.semanticweb.processor.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 默认管道实现
 * 按顺序执行各个处理模块，完成文本处理和因果关系提取
 */
public class DefaultPipeline implements Pipeline {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPipeline.class);
    
    private final TextPreprocessor preprocessor;
    private final NLPParser parser;
    private final CausalRelationExtractor causalExtractor;
    private final TemporalProcessor temporalProcessor;
    private final FinancialDomainAdapter financialDomainAdapter;
    
    /**
     * 构造函数
     */
    public DefaultPipeline(
            TextPreprocessor preprocessor,
            NLPParser parser,
            CausalRelationExtractor causalExtractor,
            TemporalProcessor temporalProcessor,
            FinancialDomainAdapter financialDomainAdapter) {
        this.preprocessor = preprocessor;
        this.parser = parser;
        this.causalExtractor = causalExtractor;
        this.temporalProcessor = temporalProcessor;
        this.financialDomainAdapter = financialDomainAdapter;
    }
    
    @Override
    public List<CausalTriple> process(String text) {
        logger.info("开始处理文本，长度: {} 字符", text.length());
        
        // 1. 文本预处理
        String preprocessedText = preprocessor.preprocess(text);
        logger.debug("预处理完成，处理后文本长度: {} 字符", preprocessedText.length());
        
        // 2. NLP解析
        Document document = parser.parse(preprocessedText);
        logger.debug("NLP解析完成，解析出 {} 个句子", document.getSentences().size());
        
        // 3. 提取因果关系
        List<CausalTriple> causalTriples = causalExtractor.extract(document);
        logger.debug("因果关系提取完成，共提取 {} 个因果关系", causalTriples.size());
        
        // 4. 时序处理
        causalTriples = temporalProcessor.process(causalTriples, document);
        logger.debug("时序处理完成，处理后共 {} 个因果关系", causalTriples.size());
        
        // 5. 金融领域适配
        causalTriples = financialDomainAdapter.adapt(causalTriples, document);
        logger.debug("金融领域适配完成，最终共 {} 个因果关系", causalTriples.size());
        
        return causalTriples;
    }
    
    @Override
    public void outputResults(List<CausalTriple> causalTriples, String outputPath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(outputPath), causalTriples);
            logger.info("结果已保存至: {}", outputPath);
        } catch (IOException e) {
            logger.error("保存结果时发生错误: {}", e.getMessage());
            throw new RuntimeException("保存结果失败", e);
        }
    }
}