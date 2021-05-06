package com.shop.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.shop.bean.UserBean;
import com.shop.exceptions.TokenUnavailableException;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Lehr
 * @create: 2020-02-04
 */
public class JwtUtils {

    /**
     * 签发对象：这个用户的id
     * 签发时间：现在
     * 有效时间：30分钟
     * 载荷内容：暂时设计为：这个人的名字，这个人的昵称
     * 加密密钥：这个人的id加上一串字符串
     */
    public static String createToken(UserBean userBean) {

        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.HOUR, 24);
        Date expiresDate = nowTime.getTime();

        return JWT.create().withAudience(userBean.getUserNo())   //签发对象
                .withIssuedAt(new Date())    //发行时间
                .withExpiresAt(expiresDate)  //有效时间
                .withClaim("userName", userBean.getUserName())    //载荷，随便写几个都可以
                .withClaim("userNo", userBean.getUserNo())
                .withClaim("userEmail", userBean.getUserEmail())
                .sign(Algorithm.HMAC256(userBean.getUserNo() + "LongRongZai"));   //加密
    }

    /**
     * 检验合法性，其中secret参数就应该传入的是用户的id
     */
    public static void verifyToken(String token, String secret) throws TokenUnavailableException {
        DecodedJWT jwt = null;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret + "LongRongZai")).build();
            jwt = verifier.verify(token);
        } catch (Exception e) {
            //效验失败
            throw new TokenUnavailableException("令牌校验失败");
        }
    }

    /**
     * 获取签发对象
     */
    public static String getAudience(String token) throws TokenUnavailableException {
        String audience = null;
        try {
            audience = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            //这里是token解析失败
            throw new TokenUnavailableException("令牌解析失败");
        }
        return audience;
    }


    /**
     * 通过载荷名字获取载荷的值
     */
    public static Claim getClaimByName(String token, String name) {
        return JWT.decode(token).getClaim(name);
    }
}

