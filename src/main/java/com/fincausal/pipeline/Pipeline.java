package com.fincausal.pipeline;

import com.fincausal.model.CausalTriple;

import java.util.List;

/**
 * 处理管道接口
 * 定义了文本处理和因果关系提取的基本流程
 */
public interface Pipeline {
    
    /**
     * 处理文本并提取因果关系
     * 
     * @param text 输入文本
     * @return 提取的因果三元组列表
     */
    List<CausalTriple> process(String text);
    
    /**
     * 将提取的因果关系输出到指定文件
     * 
     * @param causalTriples 因果三元组列表
     * @param outputPath 输出文件路径
     */
    void outputResults(List<CausalTriple> causalTriples, String outputPath);
}