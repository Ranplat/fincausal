package com.fincausal.processor;

import com.fincausal.model.CausalTriple;
import com.fincausal.model.Document;
import com.fincausal.model.Sentence;
import com.fincausal.util.ConfigLoader;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 因果关系提取器
 * 负责从解析后的文档中提取因果关系
 */
public class CausalRelationExtractor {
    private static final Logger logger = LoggerFactory.getLogger(CausalRelationExtractor.class);
    
    // 因果关系标记词
    private static final Set<String> CAUSAL_MARKERS = new HashSet<>(Arrays.asList(
            "因为", "由于", "导致", "引起", "致使", "使得", "所以", "因此", "造成",
            "引发", "促使", "促进", "带来", "产生", "形成", "决定", "影响", "源于",
            "基于", "取决于", "归因于", "缘于", "出于", "鉴于", "考虑到"
    ));
    
    // 因果关系模式
    private static final List<Pattern> CAUSAL_PATTERNS = Arrays.asList(
            // 因为A，所以B
            Pattern.compile("因为(.+?)，\\s*所以(.+)"),
            // 由于A，B
            Pattern.compile("由于(.+?)，(.+)"),
            // A导致B
            Pattern.compile("(.+?)导致(.+)"),
            // A引起B
            Pattern.compile("(.+?)引起(.+)"),
            // A致使B
            Pattern.compile("(.+?)致使(.+)"),
            // A使得B
            Pattern.compile("(.+?)使得(.+)"),
            // 因A而B
            Pattern.compile("因(.+?)而(.+)")
    );
    
    // 置信度阈值
    private final double confidenceThreshold;
    
    /**
     * 构造函数
     */
    public CausalRelationExtractor() {
        // 从配置中获取置信度阈值
        this.confidenceThreshold = ConfigLoader.getDoubleProperty("causal.confidence.threshold", 0.5);
    }
    
    /**
     * 从文档中提取因果关系
     * 
     * @param document 解析后的文档
     * @return 提取的因果三元组列表
     */
    public List<CausalTriple> extract(Document document) {
        if (document == null) {
            logger.warn("文档为空，无法提取因果关系");
            return Collections.emptyList();
        }
        
        List<CausalTriple> causalTriples = new ArrayList<>();
        
        // 遍历文档中的每个句子
        for (Sentence sentence : document.getSentences()) {
            // 1. 基于模式匹配提取因果关系
            List<CausalTriple> patternTriples = extractByPatterns(sentence);
            causalTriples.addAll(patternTriples);
            
            // 2. 基于依存句法分析提取因果关系
            List<CausalTriple> dependencyTriples = extractByDependencyParse(sentence);
            causalTriples.addAll(dependencyTriples);
        }
        
        // 过滤低置信度的因果关系
        causalTriples = filterByConfidence(causalTriples);
        
        logger.info("共提取 {} 个因果关系", causalTriples.size());
        return causalTriples;
    }
    
    /**
     * 基于模式匹配提取因果关系
     */
    private List<CausalTriple> extractByPatterns(Sentence sentence) {
        List<CausalTriple> triples = new ArrayList<>();
        String text = sentence.getText();
        
        // 检查句子是否包含因果关系标记词
        boolean containsMarker = false;
        for (String marker : CAUSAL_MARKERS) {
            if (text.contains(marker)) {
                containsMarker = true;
                break;
            }
        }
        
        // 如果不包含标记词，则跳过
        if (!containsMarker) {
            return triples;
        }
        
        // 应用因果关系模式
        for (Pattern pattern : CAUSAL_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String cause = matcher.group(1).trim();
                String effect = matcher.group(2).trim();
                String relationType = "CAUSES"; // 默认关系类型
                
                // 计算置信度（简单实现，可以根据需要改进）
                double confidence = 0.7; // 模式匹配的基础置信度
                
                // 创建因果三元组
                CausalTriple triple = new CausalTriple(cause, effect, relationType, confidence);
                triples.add(triple);
                
                logger.debug("通过模式匹配提取到因果关系: {}", triple);
            }
        }
        
        return triples;
    }
    
    /**
     * 基于依存句法分析提取因果关系
     */
    private List<CausalTriple> extractByDependencyParse(Sentence sentence) {
        List<CausalTriple> triples = new ArrayList<>();
        
        // 获取依存句法图
        SemanticGraph dependencyGraph = sentence.getDependencyParse();
        if (dependencyGraph == null) {
            return triples;
        }
        
        // 遍历依存关系边
        for (SemanticGraphEdge edge : dependencyGraph.edgeListSorted()) {
            // 获取关系类型
            GrammaticalRelation relation = edge.getRelation();
            String relName = relation.getShortName();
            
            // 检查是否是因果相关的依存关系
            if (isCausalRelation(relName)) {
                // 获取依存关系的头节点和依赖节点
                IndexedWord gov = edge.getGovernor();
                IndexedWord dep = edge.getDependent();
                
                // 提取原因和结果
                String cause = extractPhrase(dependencyGraph, dep);
                String effect = extractPhrase(dependencyGraph, gov);
                String relationType = "CAUSES";
                
                // 计算置信度
                double confidence = 0.6; // 依存分析的基础置信度
                
                // 创建因果三元组
                CausalTriple triple = new CausalTriple(cause, effect, relationType, confidence);
                triples.add(triple);
                
                logger.debug("通过依存句法分析提取到因果关系: {}", triple);
            }
        }
        
        return triples;
    }
    
    /**
     * 判断依存关系是否表示因果关系
     */
    private boolean isCausalRelation(String relation) {
        // 可能表示因果关系的依存关系类型
        return relation.equals("conj:因为") || 
               relation.equals("conj:所以") || 
               relation.equals("advmod") || 
               relation.equals("mark");
    }
    
    /**
     * 从依存图中提取以给定节点为根的短语
     */
    private String extractPhrase(SemanticGraph graph, IndexedWord root) {
        // 简单实现，仅返回单词本身
        // 实际应用中应该递归提取完整短语
        return root.word();
    }
    
    /**
     * 过滤低置信度的因果关系
     */
    private List<CausalTriple> filterByConfidence(List<CausalTriple> triples) {
        List<CausalTriple> filteredTriples = new ArrayList<>();
        
        for (CausalTriple triple : triples) {
            if (triple.getConfidence() >= confidenceThreshold) {
                filteredTriples.add(triple);
            }
        }
        
        return filteredTriples;
    }
}