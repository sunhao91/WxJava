package me.chanjar.weixin.mp.util.requestexecuter.material;

import com.google.common.collect.ImmutableMap;
import jodd.http.HttpConnectionProvider;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.http.ProxyInfo;
import jodd.util.StringPool;

import me.chanjar.weixin.common.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.http.RequestHttp;
import me.chanjar.weixin.common.util.json.WxGsonBuilder;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialNews;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by ecoolper on 2017/5/5.
 */
public class MaterialNewsInfoJoddHttpRequestExecutor extends MaterialNewsInfoRequestExecutor<HttpConnectionProvider, ProxyInfo> {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  public MaterialNewsInfoJoddHttpRequestExecutor(RequestHttp requestHttp) {
    super(requestHttp);
  }

  @Override
  public WxMpMaterialNews execute(String uri, String materialId, WxType wxType) throws WxErrorException, IOException {
    if (requestHttp.getRequestHttpProxy() != null) {
      requestHttp.getRequestHttpClient().useProxy(requestHttp.getRequestHttpProxy());
    }

    HttpRequest request = HttpRequest.post(uri)
      .withConnectionProvider(requestHttp.getRequestHttpClient())
      .body(WxGsonBuilder.create().toJson(ImmutableMap.of("media_id", materialId)));
    HttpResponse response = request.send();
    response.charset(StringPool.UTF_8);

    String responseContent = response.bodyText();
    this.logger.debug("响应原始数据：{}", responseContent);
    WxError error = WxError.fromJson(responseContent, WxType.MP);
    if (error.getErrorCode() != 0) {
      throw new WxErrorException(error);
    } else {
      return WxMpGsonBuilder.create().fromJson(responseContent, WxMpMaterialNews.class);
    }
  }
}
