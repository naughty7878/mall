package com.test.mall.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Collections;

public class GeneratorUtil {

    public static void main(String[] args) {
        String url = "jdbc:mysql://192.168.1.170:3306/mall_admin?allowPublicKeyRetrieval=true&useSSL=false";
        String username = "root";
        String password = "123456";
        String outputDir = "C:\\Users\\H__D\\Desktop";
        String xmlPath = "C:\\Users\\H__D\\Desktop";

        // 配置数据源
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(url, username, password)
                .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    String type = metaInfo.getJdbcType().name().toLowerCase();
                    if (type.contains("tinyint")) {
                        return DbColumnType.INTEGER; // 自定义 tinyint 转换为 Integer
                    }
                    // 使用默认的类型转换逻辑
                    return typeRegistry.getColumnType(metaInfo);

                });

        FastAutoGenerator.create(dataSourceConfigBuilder)
                .globalConfig(builder -> {
                    builder.author("H__D")                  // 设置作者
//                            .enableSwagger()              // 开启 swagger 模式
                            .outputDir(outputDir);          // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com")                   // 设置父包名
                            .moduleName("hd.mall.admin")           // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, xmlPath)); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder
//                            .addInclude("sys_server")        // 设置需要生成的表名
                            .addTablePrefix("sys_", "c_")  // 设置过滤表前缀

                            .entityBuilder()                // 实体策略配置
//                            .enableColumnConstant()       // 开启生成字段常量    默认值:false， 生成：public static final String LAST_NAME = "last_name";
//                            .enableChainModel()           // 开启链式模型    默认值:false 生成：@Accessors(chain = true)
//                            .enableFileOverride()         // 覆盖已生成文件    默认值:false
                            .enableLombok()                 // 开启 lombok 模型    默认值:false
                            .addTableFills(
                                    new Column("create_time", FieldFill.INSERT),    // 创建时间自动填充
                                    new Column("create_by", FieldFill.INSERT),      // 创建人自动填充
                                    new Column("update_time", FieldFill.INSERT_UPDATE), // 更新时间自动填充
                                    new Column("update_by", FieldFill.INSERT_UPDATE)    // 更新人自动填充
                            )
//                            .enableTableFieldAnnotation()    // 开启生成实体时生成字段注解    默认值:false, 生成：@TableField("last_name")
                            .mapperBuilder()                // mapper 策略配置
                            .superClass(BaseMapper.class)   // 设置父类
                            .enableMapperAnnotation()       // 开启 @Mapper 注解    默认值:false
                            .enableBaseResultMap()          // 启用 BaseResultMap 生成    默认值:false
                            .enableBaseColumnList()         // 启用 BaseColumnList    默认值:false
                    ;

                })

                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }


}