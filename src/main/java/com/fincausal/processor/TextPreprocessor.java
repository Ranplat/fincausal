package com.fincausal.processor;

import com.fincausal.util.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本预处理器
 * 负责对输入文本进行预处理，包括清洗、标准化等
 */
public class TextPreprocessor {
    private static final Logger logger = LoggerFactory.getLogger(TextPreprocessor.class);
    
    // 配置项
    private final boolean keepPunctuation;
    private final boolean removeNumbers;
    private final boolean removeSpecialChars;
    private final boolean normalizeToChinese;
    
    // 特殊字符模式
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\s]");
    
    // 数字模式
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    
    // 标点符号模式
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("\\p{P}");
    
    // 全角半角转换映射
    private static final String FULL_WIDTH_CHARS = "！＂＃＄％＆＇（）＊＋，－．／０１２３４５６７８９：；＜＝＞？＠ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ［＼］＾＿｀ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ｛｜｝～";
    private static final String HALF_WIDTH_CHARS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";
    
    /**
     * 构造函数
     */
    public TextPreprocessor() {
        // 从配置中获取预处理选项
        this.keepPunctuation = ConfigLoader.getBooleanProperty("preprocess.keep.punctuation", true);
        this.removeNumbers = ConfigLoader.getBooleanProperty("preprocess.remove.numbers", false);
        this.removeSpecialChars = ConfigLoader.getBooleanProperty("preprocess.remove.special.chars", true);
        this.normalizeToChinese = ConfigLoader.getBooleanProperty("preprocess.normalize.to.chinese", true);
    }
    
    /**
     * 预处理文本
     * 
     * @param text 输入文本
     * @return 预处理后的文本
     */
    public String preprocess(String text) {
        if (text == null || text.isEmpty()) {
            logger.warn("输入文本为空");
            return "";
        }
        
        logger.debug("开始预处理文本，长度: {} 字符", text.length());
        
        // 1. 转换全角字符为半角字符
        String processedText = normalizeToChinese ? convertToFullWidth(text) : text;
        
        // 2. 移除特殊字符
        if (removeSpecialChars) {
            processedText = removeSpecialCharacters(processedText);
        }
        
        // 3. 移除数字
        if (removeNumbers) {
            processedText = removeNumbers(processedText);
        }
        
        // 4. 移除标点符号
        if (!keepPunctuation) {
            processedText = removePunctuation(processedText);
        }
        
        logger.debug("预处理完成，处理后文本长度: {} 字符", processedText.length());
        return processedText;
    }
    
    /**
     * 移除特殊字符
     */
    private String removeSpecialCharacters(String text) {
        return SPECIAL_CHARS_PATTERN.matcher(text).replaceAll(" ");
    }
    
    /**
     * 移除数字
     */
    private String removeNumbers(String text) {
        return NUMBER_PATTERN.matcher(text).replaceAll(" ");
    }
    
    /**
     * 移除标点符号
     */
    private String removePunctuation(String text) {
        return PUNCTUATION_PATTERN.matcher(text).replaceAll(" ");
    }
    
    /**
     * 转换为全角字符
     */
    private String convertToFullWidth(String text) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int index = HALF_WIDTH_CHARS.indexOf(c);
            if (index != -1) {
                result.append(FULL_WIDTH_CHARS.charAt(index));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}