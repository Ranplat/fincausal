package com.semanticweb;

import com.semanticweb.model.CausalTriple;
import com.semanticweb.pipeline.Pipeline;
import com.semanticweb.pipeline.PipelineBuilder;
import com.semanticweb.util.ConfigLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 因果关系提取程序的主类
 * 负责程序的入口点和整体流程控制
 */
public class CausalRelationExtractor {
    private static final Logger logger = LoggerFactory.getLogger(CausalRelationExtractor.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("用法: java -jar causal-relation-extraction.jar <输入文件路径> [输出文件路径]");
            System.exit(1);
        }

        String inputFilePath = args[0];
        String outputFilePath = args.length > 1 ? args[1] : "output.json";

        try {
            // 加载配置
            ConfigLoader configLoader = new ConfigLoader();
            configLoader.loadConfig("config.properties");

            // 读取输入文本
            String text = new String(Files.readAllBytes(Paths.get(inputFilePath)));
            
            // 构建处理管道
            Pipeline pipeline = new PipelineBuilder()
                    .withPreprocessor()
                    .withParser()
                    .withCausalExtractor()
                    .withTemporalProcessor()
                    .withFinancialDomainAdapter()
                    .build();
            
            // 处理文本并提取因果关系
            List<CausalTriple> causalTriples = pipeline.process(text);
            
            // 输出结果
            pipeline.outputResults(causalTriples, outputFilePath);
            
            logger.info("处理完成，共提取 {} 个因果关系，结果已保存至 {}", causalTriples.size(), outputFilePath);
            
        } catch (IOException e) {
            logger.error("处理文件时发生错误: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("程序执行过程中发生错误: {}", e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}