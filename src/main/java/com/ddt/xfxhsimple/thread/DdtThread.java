package com.ddt.xfxhsimple.thread;


import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;
import java.util.concurrent.Callable;


@Slf4j
@Data
public class DdtThread implements Callable<String> {


    private String message;//可以是字符串
    private Map<String, Object> resultMap;

    public DdtThread(){

    }
    public DdtThread(String message){
        this.message=message;
    }


    @Override
    public String call() throws Exception {
        return callWithMessage(message);
    }

    @Value("${constants.apiKey}")
    private String apiKey;
    public  String callWithMessage( String text) throws NoApiKeyException, ApiException, InputRequiredException {
        Constants.apiKey = "";//这里需要填写apikey
        Generation gen = new Generation();
        MessageManager msgManager = new MessageManager(10);
        Message userMsg = Message.builder().role(Role.USER.getValue()).content(text).build();
        msgManager.add(userMsg);
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_TURBO).messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .enableSearch(true)
                .build();
        GenerationResult result = gen.call(param);
        if("".equals(result.getOutput().getChoices().get(0))){
            return "";
        }else {
            GenerationOutput.Choice choice = result.getOutput().getChoices().get(0);
            return choice.getMessage().getContent();
        }
    }
}
