package com.fincausal.processor;

import com.fincausal.model.CausalTriple;
import com.fincausal.model.Document;
import com.fincausal.model.Sentence;
import com.fincausal.util.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时序处理器
 * 负责处理因果关系中的时序信息
 */
public class TemporalProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TemporalProcessor.class);
    
    // 时序标记词
    private static final Map<String, String> TEMPORAL_MARKERS = new HashMap<>();
    
    // 时序关系模式
    private static final List<Pattern> TEMPORAL_PATTERNS = new ArrayList<>();
    
    /**
     * 构造函数
     */
    public TemporalProcessor() {
        // 初始化时序标记词
        initTemporalMarkers();
        // 初始化时序关系模式
        initTemporalPatterns();
    }
    
    /**
     * 初始化时序标记词
     */
    private void initTemporalMarkers() {
        // before 关系
        TEMPORAL_MARKERS.put("之前", "BEFORE");
        TEMPORAL_MARKERS.put("以前", "BEFORE");
        TEMPORAL_MARKERS.put("先于", "BEFORE");
        TEMPORAL_MARKERS.put("早于", "BEFORE");
        TEMPORAL_MARKERS.put("先", "BEFORE");
        TEMPORAL_MARKERS.put("预先", "BEFORE");
        
        // after 关系
        TEMPORAL_MARKERS.put("之后", "AFTER");
        TEMPORAL_MARKERS.put("以后", "AFTER");
        TEMPORAL_MARKERS.put("后于", "AFTER");
        TEMPORAL_MARKERS.put("晚于", "AFTER");
        TEMPORAL_MARKERS.put("随后", "AFTER");
        TEMPORAL_MARKERS.put("后来", "AFTER");
        
        // during 关系
        TEMPORAL_MARKERS.put("期间", "DURING");
        TEMPORAL_MARKERS.put("过程中", "DURING");
        TEMPORAL_MARKERS.put("同时", "DURING");
        TEMPORAL_MARKERS.put("当时", "DURING");
        TEMPORAL_MARKERS.put("正在", "DURING");
        
        // starts 关系
        TEMPORAL_MARKERS.put("开始", "STARTS");
        TEMPORAL_MARKERS.put("起初", "STARTS");
        TEMPORAL_MARKERS.put("最初", "STARTS");
        
        // ends 关系
        TEMPORAL_MARKERS.put("结束", "ENDS");
        TEMPORAL_MARKERS.put("最终", "ENDS");
        TEMPORAL_MARKERS.put("最后", "ENDS");
        TEMPORAL_MARKERS.put("后于", "AFTER");
        TEMPORAL_MARKERS.put("晚于", "AFTER");
        
        // 同时关系
        TEMPORAL_MARKERS.put("同时", "SIMULTANEOUS");
        TEMPORAL_MARKERS.put("期间", "DURING");
        TEMPORAL_MARKERS.put("过程中", "DURING");
        
        // 其他时序关系
        TEMPORAL_MARKERS.put("随着", "WITH");
        TEMPORAL_MARKERS.put("伴随", "WITH");
    }
    
    /**
     * 初始化时序关系模式
     */
    private void initTemporalPatterns() {
        // 在...之前
        TEMPORAL_PATTERNS.add(Pattern.compile("在(.+?)之前"));
        // 在...之后
        TEMPORAL_PATTERNS.add(Pattern.compile("在(.+?)之后"));
        // 在...期间
        TEMPORAL_PATTERNS.add(Pattern.compile("在(.+?)期间"));
        // 从...开始
        TEMPORAL_PATTERNS.add(Pattern.compile("从(.+?)开始"));
        // 到...结束
        TEMPORAL_PATTERNS.add(Pattern.compile("到(.+?)结束"));
        // 先...后...
        TEMPORAL_PATTERNS.add(Pattern.compile("先(.+?)后(.+?)"));
        // 在A之前，B
        TEMPORAL_PATTERNS.add(Pattern.compile("在(.+?)之前，(.+)"));
        // A之后，B
        TEMPORAL_PATTERNS.add(Pattern.compile("(.+?)之后，(.+)"));
        // 随着A，B
        TEMPORAL_PATTERNS.add(Pattern.compile("随着(.+?)，(.+)"));
    }
    
    /**
     * 处理因果关系中的时序信息
     * 
     * @param causalTriples 因果三元组列表
     * @return 处理后的因果三元组列表
     */
    public List<CausalTriple> process(List<CausalTriple> causalTriples, Document document) {
        if (causalTriples == null || causalTriples.isEmpty()) {
            logger.warn("因果三元组列表为空");
            return new ArrayList<>();
        }
        
        logger.debug("开始处理时序信息，三元组数量: {}", causalTriples.size());
        
        List<CausalTriple> processedTriples = new ArrayList<>();
        
        for (CausalTriple triple : causalTriples) {
            // 处理原因中的时序信息
            String cause = triple.getCause();
            String temporalRelation = extractTemporalRelation(cause);
            
            if (temporalRelation != null) {
                triple.setTemporalRelation(temporalRelation);
            }
            
            // 处理结果中的时序信息
            String effect = triple.getEffect();
            temporalRelation = extractTemporalRelation(effect);
            
            if (temporalRelation != null && triple.getTemporalRelation() == null) {
                triple.setTemporalRelation(temporalRelation);
            }
            
            // 如果没有找到明确的时序关系，默认为AFTER（因为通常原因发生在结果之前）
            if (triple.getTemporalRelation() == null) {
                triple.setTemporalRelation("AFTER");
            }
            
            processedTriples.add(triple);
        }
        
        logger.info("时序信息处理完成，处理后三元组数量: {}", processedTriples.size());
        return processedTriples;
    }
    
    /**
     * 根据模式确定时序关系
     */
    private String determineTemporalRelation(String patternStr, String event1, String event2) {
        if (patternStr.contains("之前")) {
            return "BEFORE";
        } else if (patternStr.contains("之后")) {
            return "AFTER";
        } else if (patternStr.contains("随着")) {
            return "WITH";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 从文本中提取时间表达式
     */
    private List<String> extractTimeExpressions(String text) {
        List<String> timeExpressions = new ArrayList<>();
        // 使用Stanford CoreNLP的TimeAnnotator提取时间表达式
        // 这里简化实现，实际应用中应该使用CoreNLP的时间表达式识别功能
        
        // 简单的时间表达式模式
        Pattern timePattern = Pattern.compile("(\\d{4}年\\d{1,2}月\\d{1,2}日|\\d{4}-\\d{1,2}-\\d{1,2}|昨天|今天|明天|上周|本周|下周|上个月|这个月|下个月)");
        Matcher matcher = timePattern.matcher(text);
        
        while (matcher.find()) {
            timeExpressions.add(matcher.group());
        }
        
        return timeExpressions;
    }

    /**
     * 从文本中提取时序关系
     * 
     * @param text 待分析的文本
     * @return 时序关系类型
     */
    private String extractTemporalRelation(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }

        // 检查时序标记词
        for (Map.Entry<String, String> entry : TEMPORAL_MARKERS.entrySet()) {
            if (text.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 检查时序模式
        for (Pattern pattern : TEMPORAL_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String event1 = matcher.group(1);
                String event2 = matcher.groupCount() > 1 ? matcher.group(2) : null;
                return determineTemporalRelation(text, event1, event2);
            }
        }

        return null;
    }
}