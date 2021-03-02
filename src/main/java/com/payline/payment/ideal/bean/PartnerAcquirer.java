package com.payline.payment.ideal.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerAcquirer {

    /** Acquirer Name **/
    @JsonProperty("NAME")
    String name;

    /** Acquirer Url **/
    @JsonProperty("URL")
    String url;

    /** Public Key shared with this acquirer **/
    @JsonProperty("PUBLIC_KEY")
    String publicKey;

    /** Acquirer Ideal public Key **/
    @JsonProperty("IDEAL_PUBLIC")
    String idealPublicKey;

    /** Private key for this acquirer. **/
    @JsonProperty("PRIVATE_KEY")
    String privateKey;

    /** Public Key identifier shared with this acquirer. **/
    @JsonProperty("PUBLIC_KEY_ID")
    String publicKeyId;

    /** MerchantId for directory requests. **/
    @JsonProperty("MERCHANT_ID")
    String merchantId;

    /** SubMerchantId for directory requests. **/
    @JsonProperty("SUBMERCHANT_ID")
    String subMerchantId;
}
