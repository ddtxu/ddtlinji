实现目标 ：希望能够以语音的形式向提问机器人提出问题，机器人语音回答提的问题
===

### 1. 准备工作

        1. 安装了FreeSWITCH   http://www.ddrj.com/callcenter/userguide.html
        2. 安装了(mod_cti基于FreeSWITCH)-语音识别（asr）接口  可在该网站申请相关接口测试 http://www.ddrj.com/callcenter/asr.html
        3. 下载ccAdmin和sipphone(方便测试)  可在该网站下载 http://www.ddrj.com/callcenter/gui.html  http://www.ddrj.com/sipphone/index.html
        4. 获取的阿里灵积大模型api_key，代码里面要填写的  从该地址可申请获取到相关的key https://help.aliyun.com/zh/dashscope/developer-reference/activate-dashscope-and-create-an-api-key?spm=a2c4g.11186623.0.0.588216e9CpLp2k
### 2. java 后端接口说明

         1. 项目说明
               这个项目是使用java 代码实现与阿里灵积大模型 通义千问 对接，实现机器人问答功能。
### 3. 下载代码后请将在阿里后台获取的阿里灵积大模型api_key 填写到代码中
### 4. 可围观CSDN [https://blog.csdn.net/qq_52528295/article/details/133741976?spm=1001.2014.3001.5502](https://blog.csdn.net/qq_52528295/article/details/134186331?spm=1001.2014.3001.5502)https://blog.csdn.net/qq_52528295/article/details/134186331?spm=1001.2014.3001.5502  里有详细介绍和解释
