package com.fincausal.processor;

import com.fincausal.model.Document;
import com.fincausal.model.Sentence;
import com.fincausal.util.ConfigLoader;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * NLP解析器
 * 负责使用Stanford CoreNLP进行自然语言处理
 */
public class NLPParser {
    private static final Logger logger = LoggerFactory.getLogger(NLPParser.class);
    
    // Stanford CoreNLP管道
    private final StanfordCoreNLP pipeline;
    
    /**
     * 构造函数
     */
    public NLPParser() {
        // 从配置中获取语言
        String language = ConfigLoader.getStringProperty("nlp.language", "zh");
        
        // 初始化Stanford CoreNLP管道
        Properties props = new Properties();
        
        if ("zh".equals(language)) {
            // 中文处理配置
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse");
            props.setProperty("tokenize.language", "zh");
            props.setProperty("segment.model", "edu/stanford/nlp/models/segmenter/chinese/ctb.gz");
            props.setProperty("segment.sighanCorporaDict", "edu/stanford/nlp/models/segmenter/chinese");
            props.setProperty("segment.serDictionary", "edu/stanford/nlp/models/segmenter/chinese/dict-chris6.ser.gz");
            props.setProperty("segment.sighanPostProcessing", "true");
            props.setProperty("ssplit.boundaryTokenRegex", "[.。]|[!?！？]+");
            props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/chinese-distsim/chinese-distsim.tagger");
            props.setProperty("ner.model", "edu/stanford/nlp/models/ner/chinese.misc.distsim.crf.ser.gz");
            props.setProperty("ner.applyNumericClassifiers", "true");
            props.setProperty("ner.useSUTime", "true");
            props.setProperty("parse.model", "edu/stanford/nlp/models/lexparser/chinesePCFG.ser.gz");
            props.setProperty("depparse.model", "edu/stanford/nlp/models/parser/nndep/UD_Chinese.gz");
        } else {
            // 英文处理配置（默认）
            props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, depparse");
        }
        
        // 创建管道
        this.pipeline = new StanfordCoreNLP(props);
        logger.info("已初始化NLP解析器，语言: {}", language);
    }
    
    /**
     * 解析文本
     * 
     * @param text 输入文本
     * @return 解析后的文档
     */
    public Document parse(String text) {
        if (text == null || text.isEmpty()) {
            logger.warn("输入文本为空");
            return new Document("");
        }
        
        logger.debug("开始解析文本，长度: {} 字符", text.length());
        
        try {
            // 创建文档
            Document document = new Document(text);
            
            // 创建注释
            Annotation annotation = new Annotation(text);
            
            // 运行所有注释器
            pipeline.annotate(annotation);
            
            // 获取句子注释
            List<CoreMap> sentenceAnnotations = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            
            // 处理每个句子
            for (int i = 0; i < sentenceAnnotations.size(); i++) {
                try {
                    CoreMap sentenceAnnotation = sentenceAnnotations.get(i);
                    String sentenceText = sentenceAnnotation.toString();
                    
                    // 创建句子对象
                    Sentence sentence = new Sentence(sentenceText, i);
                    
                    // 提取分词结果
                    List<CoreLabel> tokens = sentenceAnnotation.get(CoreAnnotations.TokensAnnotation.class);
                    if (tokens != null) {
                        for (CoreLabel token : tokens) {
                            try {
                                // 添加分词
                                sentence.addToken(token.word());
                                
                                // 添加词元
                                sentence.addLemma(token.lemma());
                                
                                // 添加词性标注
                                sentence.addPosTag(token.tag());
                                
                                // 添加命名实体识别标注
                                sentence.addNerTag(token.ner());
                            } catch (Exception e) {
                                logger.warn("处理词条时发生错误: {}", e.getMessage());
                            }
                        }
                    }
                    
                    try {
                        // 提取句法分析树
                        Tree parseTree = sentenceAnnotation.get(TreeCoreAnnotations.TreeAnnotation.class);
                        if (parseTree != null) {
                            sentence.setParseTree(parseTree);
                        } else {
                            logger.warn("句子{}的句法分析树为空", i);
                        }
                    } catch (Exception e) {
                        logger.error("提取句法分析树时发生错误: {}", e.getMessage());
                    }
                    
                    try {
                        // 提取依存句法分析图
                        SemanticGraph dependencyParse = sentenceAnnotation.get(SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation.class);
                        if (dependencyParse != null) {
                            sentence.setDependencyParse(dependencyParse);
                        } else {
                            logger.warn("句子{}的依存句法分析图为空", i);
                        }
                    } catch (Exception e) {
                        logger.error("提取依存句法分析图时发生错误: {}", e.getMessage());
                    }
                    
                    // 将句子添加到文档
                    document.addSentence(sentence);
                } catch (Exception e) {
                    logger.error("处理句子{}时发生错误: {}", i, e.getMessage());
                }
            }
            
            logger.info("文本解析完成，共 {} 个句子", document.getSentences().size());
            return document;
        } catch (Exception e) {
            logger.error("解析文本时发生错误: {}", e.getMessage());
            return new Document(text);
        }
    }
     }