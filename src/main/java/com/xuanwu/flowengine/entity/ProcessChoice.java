package com.xuanwu.flowengine.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 审批选项定义
 * File created on 2017/5/22.
 *
 * @author jkun
 */
public class ProcessChoice {

    private static final Logger logger = LoggerFactory.getLogger(ProcessChoice.class);
    public static final String CHOICE_VARIABLE_KEY = "choice";
    public static final String CHOICENAME_VARIABLE_KEY = "choicename";

    /**
     * 标识
     */
    @JsonProperty("af_choice")
    private int code;

    /**
     * 值
     */
    @JsonProperty("af_choicename")
    private String value;

    public ProcessChoice(int code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 获取默认的审批选项
     * @return
     */
    public static Collection<ProcessChoice> getDefaultChoice() {
        ProcessChoice agree = new ProcessChoice(1, "同意");
        ProcessChoice disagree = new ProcessChoice(2, "不同意");

        return Arrays.asList(agree, disagree);
    }

    /**
     * 解析流程配置里的choice串
     *
     * @param choiceConfig
     * @return
     */
    public static Collection<ProcessChoice> splitChoicesFromString(String choiceConfig) {
        if (StringUtils.isNotEmpty(choiceConfig)) {
            try {
                String regEx = "\\[(\\d\\|.+?)\\]";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(choiceConfig);
                List<ProcessChoice> choices = new ArrayList<>();
                ProcessChoice singleChoice;
                while (matcher.find()) {
                    String matchStr = matcher.group(matcher.groupCount());
                    String[] strArray = StringUtils.split(matchStr, "|");
                    if (null != strArray && strArray.length == 2) {
                        singleChoice = new ProcessChoice(Integer.valueOf(strArray[0]), strArray[1]);
                        choices.add(singleChoice);
                    }
                }

                if (!choices.isEmpty()) {
                    return choices;
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }

        return getDefaultChoice();
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
