package com.hhgs.shows.util;

import com.alibaba.fastjson.JSONObject;
import com.hhgs.shows.model.BO.DolphinDBResult;
import com.hhgs.shows.model.BO.LoginCheckResult;
import com.hhgs.shows.model.DO.QueryObjectData;

public class SelectDataFromDolphindb {


    private static final String URL = "http://172.28.8.25/avatar-datamanage-service/data/queryMetaObjData?metaObjectName=";

    private static final String SUCCESS_CODE = "0";

    public static DolphinDBResult getDataFromInfluxdb(String dataObjectName, long startTime, long endTime, long id) {

        String p = "{'starttime': '" + startTime + "ms','id':'" + id + "','fillPoint':'" + 0 + "','sampled':'" + 60 + "','endtime': '" + endTime + "ms'}";

        QueryObjectData param = new QueryObjectData(p, null);

        //调用阿凡达接口，首先需要获取token
        LoginCheckResult result = null;
        try {
            result = LoginCheckUtils.LoginCheck();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!SUCCESS_CODE.equals(result.getStatusCode())) {
            return null;
        }

        String token = result.getData();

        //调用阿凡达接口
        String json = HttpUtils.getInstance().doPostByToken(URL, token, dataObjectName, param);

        DolphinDBResult dolphinDBResult = JSONObject.parseObject(json, DolphinDBResult.class);

        if (SUCCESS_CODE.equals(dolphinDBResult.getStatusCode())) {
            return dolphinDBResult;
        }

        return null;
    }
}
