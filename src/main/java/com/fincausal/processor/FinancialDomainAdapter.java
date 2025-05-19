package com.fincausal.processor;

import com.fincausal.model.CausalTriple;
import com.fincausal.model.Document;
import com.fincausal.util.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 金融领域适配器
 * 负责处理金融领域特定的适配，包括金融术语识别和分类
 */
public class FinancialDomainAdapter {
    private static final Logger logger = LoggerFactory.getLogger(FinancialDomainAdapter.class);

    /**
     * 适配金融领域的因果关系
     * 
     * @param causalTriples 因果三元组列表
     * @param document 文档信息
     * @return 适配后的因果三元组列表
     */
    public List<CausalTriple> adapt(List<CausalTriple> causalTriples, Document document) {
        if (causalTriples == null || causalTriples.isEmpty()) {
            logger.warn("因果三元组列表为空");
            return new ArrayList<>();
        }

        logger.debug("开始进行金融领域适配，三元组数量: {}", causalTriples.size());

        List<CausalTriple> adaptedTriples = new ArrayList<>();
        for (CausalTriple triple : causalTriples) {
            // 在这里添加金融领域特定的适配逻辑
            // 例如：识别金融实体、标准化金融术语、添加金融属性等
            adaptedTriples.add(triple);
        }

        logger.info("金融领域适配完成，适配后三元组数量: {}", adaptedTriples.size());
        return adaptedTriples;
    }
    // 金融领域词典
    private final Map<String, String> financialTerms = new HashMap<>();
    
    // 金融领域分类
    private static final Map<String, List<String>> FINANCIAL_CATEGORIES = new HashMap<>();
    
    /**
     * 构造函数
     */
    public FinancialDomainAdapter() {
        // 初始化金融领域分类
        initFinancialCategories();
        
        // 加载金融领域词典
        loadFinancialDictionary();
    }
    
    /**
     * 初始化金融领域分类
     */
    private void initFinancialCategories() {
        // 市场表现类
        FINANCIAL_CATEGORIES.put("MARKET_PERFORMANCE", Arrays.asList(
                "股价", "市值", "涨幅", "跌幅", "波动", "行情", "指数", "大盘", "牛市", "熊市"
        ));
        
        // 财务指标类
        FINANCIAL_CATEGORIES.put("FINANCIAL_METRICS", Arrays.asList(
                "营收", "利润", "净利", "毛利", "收入", "成本", "费用", "资产", "负债", "现金流",
                "ROE", "ROA", "EPS", "PE", "PB", "市盈率", "市净率", "资产负债率"
        ));
        
        // 公司运营类
        FINANCIAL_CATEGORIES.put("COMPANY_OPERATION", Arrays.asList(
                "销售", "产能", "产量", "库存", "研发", "投资", "并购", "重组", "扩张", "收缩",
                "转型", "升级", "创新", "效率", "产业链", "供应链"
        ));
        
        // 宏观经济类
        FINANCIAL_CATEGORIES.put("MACRO_ECONOMY", Arrays.asList(
                "GDP", "CPI", "PPI", "PMI", "利率", "汇率", "通胀", "通缩", "货币政策", "财政政策",
                "经济增长", "经济衰退", "经济复苏", "贸易战", "贸易摩擦"
        ));
        
        // 政策监管类
        FINANCIAL_CATEGORIES.put("POLICY_REGULATION", Arrays.asList(
                "政策", "监管", "法规", "条例", "规定", "措施", "整改", "处罚", "合规", "违规",
                "审批", "备案", "许可", "禁止", "限制", "准入", "退出", "监督"
        ));
    }
    
    /**
     * 加载金融领域词典
     */
    private void loadFinancialDictionary() {
        String dictionaryPath = ConfigLoader.getStringProperty("financial.dictionary.path", "dictionary/financial_terms.txt");
        
        try {
            // 首先尝试从文件系统加载
            if (Files.exists(Paths.get(dictionaryPath))) {
                try (BufferedReader reader = Files.newBufferedReader(Paths.get(dictionaryPath), StandardCharsets.UTF_8)) {
                    loadDictionaryFromReader(reader);
                }
            } else {
                // 如果文件系统中不存在，尝试从类路径加载
                try (InputStream is = getClass().getClassLoader().getResourceAsStream(dictionaryPath)) {
                    if (is != null) {
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                            loadDictionaryFromReader(reader);
                        }
                    } else {
                        logger.warn("金融领域词典文件不存在: {}", dictionaryPath);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("加载金融领域词典时发生错误: {}", e.getMessage());
        }
    }
    
    /**
     * 从Reader加载词典
     * 
     * @param reader BufferedReader实例
     * @throws IOException 如果读取过程中发生IO错误
     */
    private void loadDictionaryFromReader(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            
            String[] parts = line.split("\\t", 2);
            if (parts.length == 2) {
                financialTerms.put(parts[0].trim(), parts[1].trim());
            } else {
                logger.warn("无效的词典条目: {}", line);
            }
        }
        logger.info("已加载{}个金融领域词条", financialTerms.size());
    }

    /**
     * 处理文本中的金融术语
     * 
     * @param text 输入文本
     * @return 识别到的金融术语及其解释的映射
     */
    public Map<String, String> processFinancialTerms(String text) {
        Map<String, String> recognizedTerms = new HashMap<>();
        
        // 对每个金融术语进行匹配
        for (Map.Entry<String, String> entry : financialTerms.entrySet()) {
            String term = entry.getKey();
            String explanation = entry.getValue();
            
            // 使用模糊匹配
            if (fuzzyMatch(text, term)) {
                recognizedTerms.put(term, explanation);
            }
        }
        
        return recognizedTerms;
    }
    
    /**
     * 对金融术语进行分类
     * 
     * @param terms 金融术语列表
     * @return 按类别分类的金融术语映射
     */
    public Map<String, List<String>> classifyFinancialTerms(List<String> terms) {
        Map<String, List<String>> categorizedTerms = new HashMap<>();
        
        // 初始化分类结果
        for (String category : FINANCIAL_CATEGORIES.keySet()) {
            categorizedTerms.put(category, new ArrayList<>());
        }
        
        // 对每个术语进行分类
        for (String term : terms) {
            for (Map.Entry<String, List<String>> entry : FINANCIAL_CATEGORIES.entrySet()) {
                String category = entry.getKey();
                List<String> categoryTerms = entry.getValue();
                
                if (categoryTerms.contains(term)) {
                    categorizedTerms.get(category).add(term);
                    break;
                }
            }
        }
        
        return categorizedTerms;
    }
    
    /**
     * 模糊匹配实现
     * 
     * @param text 文本
     * @param term 待匹配的术语
     * @return 是否匹配
     */
    private boolean fuzzyMatch(String text, String term) {
        // 计算编辑距离的阈值，根据术语长度动态调整
        int threshold = Math.max(1, term.length() / 4);
        
        // 使用滑动窗口进行匹配
        int windowSize = term.length() + threshold;
        for (int i = 0; i <= text.length() - term.length(); i++) {
            String window = text.substring(i, Math.min(i + windowSize, text.length()));
            if (calculateLevenshteinDistance(window, term) <= threshold) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 计算编辑距离
     * 
     * @param s1 字符串1
     * @param s2 字符串2
     * @return 编辑距离
     */
    private int calculateLevenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                                   dp[i - 1][j - 1] + cost);
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
}