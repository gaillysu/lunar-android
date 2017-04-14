package com.medcorp.lunar.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.medcorp.lunar.application.ApplicationModel;
import com.medcorp.lunar.event.WeChatTokenEvent;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Jason on 2017/3/31.
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationModel.getInstance().getWXApi().handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ApplicationModel.getInstance().getWXApi().handleIntent(intent, WXEntryActivity.this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp instanceof SendAuth.Resp) {
            int errCode = baseResp.errCode;
            switch (errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    //用户同意
                    String code = ((SendAuth.Resp) baseResp).code;
                    Log.i("jason", "code=====||||||" + code);
                    EventBus.getDefault().post(new WeChatTokenEvent(code));
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    //用户拒绝
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    //用户取消
                    break;
            }
            finish();
        }
    }
}
