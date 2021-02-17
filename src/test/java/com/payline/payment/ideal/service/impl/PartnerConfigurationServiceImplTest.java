package com.payline.payment.ideal.service.impl;

import com.payline.payment.ideal.bean.PartnerAcquirer;
import com.payline.payment.ideal.service.PartnerConfigurationService;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;


class PartnerConfigurationServiceImplTest {

    final PartnerConfigurationService underTest = new PartnerConfigurationServiceImpl();

    @Test
     void testFetchAcquirerList() {
        final Map<String, String> data = new HashMap<>();
        final Map<String, String> sensitiveData = new HashMap<>();
        data.put("acquirers", "[\n" +
                "         {\n" +
                "            \"NAME\":\"BNPP\",\n" +
                "            \"URL\":\"https://ecommerce-test.bnpparibas.com/bvn-idx-iDEAL-rs/iDEALv3\",\n" +
                "            \"PUBLIC_KEY\":\"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAubmR6G8yJFBvwB9+rF0zIz99s33zY9aHDR5o6fPKY6M9sE9LfoSUcSmJAqdJNypF2WvXaLpNCgL4jXifjN68+dVE89Tf2eC593vsZduIxwyQGs67CtjWZ7uxXwTQWn1paF4swV3FnPytNagB0WD3riIcc0eg2p/E8/pSE+NCOG6CSwfhdeegzO9fLdevIHSt+UK4P5EezrEC9ve1fvyzsaSOUCAnkD9SwtSzMgikX5+ywJPmSqKd+ztpB3iKL6kJXspwoDgkp9a/frQVeggaENAvLSwbOCTyXS/Fsy/4yZuTQLBsNJkVhE8cmTt5QKhf2XNrfsngE1JThUY9iuREZQIDAQAB-----END PUBLIC KEY-----\",\n" +
                "            \"IDEAL_PUBLIC\":\"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtnKKgKzmQi2iB8D7u8FJ6fGF2lL1+/z0Cc73tFHOVIgPcK6BTv63xgnUDYOi1iS6WigK+Vv163liXCFCgGh2VmBHMRRq2brLdFGEcAEpfTBZYaW2LKnEcT3HxosjyYR4U02JPy8tKJrsWceDePycOv++TwRVmiesWPVfOmoj9vhVABr6p8A9A1pA9Pt8oQzGV2p7zoEhgRJe0rRTJWtROjCQo9gVghsMTXv785STLxNi76o9Z1tsgiDdTq/S1VQjv4rJh5Yxdp3ml3NKctvhA6hkh3udghqFLSbWux05M860mctNxHIlaw1G0YitXUwp4odoBkJr8d5cZmpW3PlP/wIDAQAB-----END PUBLIC KEY-----\",\n" +
                "            \"PUBLIC_KEY_ID\":\"DABF60FE0A409253350CBE72E375D4EE8F7B09EB\",\n" +
                "            \"MERCHANT_ID\":\"017001580\",\n" +
                "            \"SUBMERCHANT_ID\":\"0\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"NAME\":\"ABNAMRO\",\n" +
                "            \"URL\":\"https://abnamro-test.ideal-payment.de/ideal/iDealv3\",\n" +
                "            \"PUBLIC_KEY\":\"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0/tsxMb/5CJTVPYrjxieO+3P6OToxIwVqWy+mevM3X+XhguY/dH29M26hF1SUPwSTtkzx7WOVR7ckBmN7nDyebdjHu0l84PsHOjCrWt0bU5cGvb00/v7gBxf+lOWnnhbJOdB84HD4Nw8C4Sws9NQSlYEQYH2vgQsyDOxq3sS1A2rXDy/3MSrKBrziILuFYySQ36h/liK6WOg1oVKVbGjd6T/s5qt9PrjCuxhKEKq+uakuxL6VKT80yCA3jPS2J8jSlmPLHEr9lWe995AxoOa+EYZOnh7VZlJundTmxp8SCY2dF3+mGPHwjSNP+0mTqq8vLTn4K75hKD9rvf1zXyHhwIDAQAB-----END PUBLIC KEY-----\",\n" +
                "            \"IDEAL_PUBLIC\":\"-----BEGIN PUBLIC KEY-----\\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr0wff1vuVT4XmmNlEid5337Nqze5e5P/j8HwkUsZA8MOKI2NDeN4H70v6fcxaYncpsNsqGM35lvxPIcI+3o3awa6aJKfI5fv9cKBx18FZs+BLWK0Bsp3wWyzG0kpaFUVZoy1JcYzgDJr7Xh2dt2hPQA8hGYwlzo2X54cnsehesv/LuJed41wBCzK3THML4FMawSie7JPLkt+FNLanGf1VI3xA1kyUjcc+FbOtwJBmbol1z5zF6I5k+hodldYiTAXZ9zHi4pAEtD8N2K+Ip2q4bhIWNoyTUj9ue/J+4Vm596nrbYovP8GVECHFrhPN66a1A0AFTpTw2z9ztd1e/LG+wIDAQAB-----END PUBLIC KEY-----\",\n" +
                "            \"PUBLIC_KEY_ID\":\"686643AF86B9BC2F442992919092A7B3835990D4\",\n" +
                "            \"MERCHANT_ID\":\"003087616\",\n" +
                "            \"SUBMERCHANT_ID\":\"0\"\n" +
                "         }\n" +
                "      ]");
                sensitiveData.put("acquirersSensitiveData", "[\n" +
                "         {\n" +
                "            \"NAME\":\"BNPP\",\n" +
                "            \"PRIVATE_KEY\":\"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC5uZHobzIkUG/AH36sXTMjP32zffNj1ocNHmjp88pjoz2wT0t+hJRxKYkCp0k3KkXZa9douk0KAviNeJ+M3rz51UTz1N/Z4Ln3e+xl24jHDJAazrsK2NZnu7FfBNBafWloXizBXcWc/K01qAHRYPeuIhxzR6Dan8Tz+lIT40I4boJLB+F156DM718t168gdK35Qrg/kR7OsQL297V+/LOxpI5QICeQP1LC1LMyCKRfn7LAk+ZKop37O2kHeIovqQleynCgOCSn1r9+tBV6CBoQ0C8tLBs4JPJdL8WzL/jJm5NAsGw0mRWETxyZO3lAqF/Zc2t+yeATUlOFRj2K5ERlAgMBAAECggEAQXlqNsb8plC1FMYFoj45CPQZHG4Kn+fBNJ7D3bZUY0vpM37A3pleu4YpBwZyiUAd38hk6EbFYgIqHbWoicD8XhcRd+RUBjtzVAwcCsyaK5ICO3El/2zyKR/CJ/1Kdpw/zMtme8TTJcTuSzwvZ+EHcBwWa42U533rsRdOWDSF4953g94I0kpaG3QtguRX1WPu37rfy9Q5jBYCP8RI+dlpseO61fAw1BeRDdapE+xbTXwTVS6j7IO4hAxubb17mzQpVWnu0nrPcQxy4b8yX2grngF9hxAmRLF3UUGfAQbbQu+BvJF8AwoW7PIFOkjDa+pzRENk5JKF+5i4+gXwpVezAQKBgQDoR9JTcMYS6n5JwtJ+FzoUQ4R7yKh4TexHOJMFEtUCLWyPa/bHhtbX7ooGaBOFPwiWKgcOYGPG4TNTJ1fSSXOeNG0z0M2/4VHU046Eflm1FMyEkeSxIBf1u+iADbXpft7TnthWqQXdUZrolQmgMHScad278lbUhxpuVhCCU9TGdwKBgQDMsLXEzC/0Sob3u6ogFgpU4fTYZrs0rrC39Oy/Vcpm7nDwx1G4ocfNYmOYdgqU6bo0gfaU9HF89GRsdI+YbGqNzqvnwMV/PKMC+gQDhmD51e4WTR8zQlYSlU0enUG5YJny6UEeiWFKvC2emxY3gSgSV5PPue3204BasbQx3wjXAwKBgQCDEDuCL2jkNYIqto407YAs/OfdPJyqvTUr/qUeQMEGKIFP79hgPxos3wDsmn8hOS7sJCy1FPhXuKxQKCvqkN13EBfqG9Bsi4Oz9ec9pVmSzniT+Qo0U2qRkoSe9J2HcbnjaIajZ13SwoAI6kdzJsQ1mZquuQ/7fXwRc+h+0TSHAwKBgC3f7N0OZjD6AwXCqX5HcklT2uwlGg/Ulwajfevj7ljjV6ye/1HB8GW3h1rEGdsGBxV9iyYXItg+bcNg3E3s/nSJvT1Tlu/NRzuDjSK9Gz7jE1ksL7HtTB0eGQqjc/d4DwjB/jY50/RL+G1zlKcb6z8xIY+MvCh1fm7VJO2IbxLBAoGBAKcxLQaTLVDjTvbGpnYhBp7HH8nM+jUor9BJjf7o0Ix/pxeop2YMz29dWke9oIBSMM8BDIvBJpYeckJHEkY5tgmwFhd9bOV1Sap6cdAFuWyVXQabY6yWQOcMdoJGw37oxujmDarHfKTyNtfCDwUQZm/kxNY7Xqq3Ys1qBgKwMMqr-----END PRIVATE KEY-----\"\n" +
                "         },\n" +
                "         {\n" +
                "            \"NAME\":\"ABNAMRO\",\n" +
                "            \"PRIVATE_KEY\":\"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDT+2zExv/kIlNU9iuPGJ477c/o5OjEjBWpbL6Z68zdf5eGC5j90fb0zbqEXVJQ/BJO2TPHtY5VHtyQGY3ucPJ5t2Me7SXzg+wc6MKta3RtTlwa9vTT+/uAHF/6U5aeeFsk50HzgcPg3DwLhLCz01BKVgRBgfa+BCzIM7GrexLUDatcPL/cxKsoGvOIgu4VjJJDfqH+WIrpY6DWhUpVsaN3pP+zmq30+uMK7GEoQqr65qS7EvpUpPzTIIDeM9LYnyNKWY8scSv2VZ733kDGg5r4Rhk6eHtVmUm6d1ObGnxIJjZ0Xf6YY8fCNI0/7SZOqry8tOfgrvmEoP2u9/XNfIeHAgMBAAECggEAaZhG+EbA1V2b8SinTiLPP78Y4ESXBsFZUQpQb9AcPwH98tH4JgffYtbQYHs0NA8893YO6x6vhgtXh5iTMQGmZ6dIgvFTBFjk1wnDYCu01XZBP1VhY++Tup9n2ASndM0zadCg87HiZzKN3pb8hrduuXjbqd4ZkVH3FMtbZ7ZpDKPR1D6ZSIAtUwaAuqKjLIVj2KsDcg4hatMomoUBY3vXaqmMLPDMaXgccIhtCazZE9BrQfRGFHywqcSfgTLrpm4TXiUavoQyLGZ1i2b0o9K5Ml2LOF6CAw3y5TWLS4M08jv2A/UVYLycw5h8fQxcriQ0VNk7F7VuvBCCFTDYlG7n0QKBgQD6eUdNcMjaLanouWCdeYTzAdZeLb69X1U8gUmcojTbENvVyG+feJM/2hjyd8MlsdllFlGwx/IahkaE58zJXMxsMOPh12+bU8dk6eteJeXCS1+FCRlugBx40Aw/qHVGpPaKR9GWx0nxFX7xGC9Fci2zpOBVC2ridbIcHOdgsLENMwKBgQDYqL0ZqS86VHMtVcPnAQ7JWalssWzv1NSCTnH9prYlRkxFNDK3FI+deheIdNrm4L7WxAwMvTeWe6wJn8ef2G+I+Q3Ylv6UaPYRIzoqe4vJbC9qccD1ZAS+kp6D90ykTch1TGiS/p086b1HS8qJeC+qZ3lcAyLCZ0CTI+m8i9JUXQKBgGlhXytouUh6D9NXMxvuBY6MBlnnWymnk1/6cTMSV7SXOjaOts/cVe92XiqdUqBoXDOYmVQMO4MHXWILeHB7t5IJW8cn8c/jBrAhbqJUxX7iYVTCiE2iXthBh/W8dWL8grGZF8gdHrHsvlTHPRQ1vp6nTq7ZN+YBaKdFTd+zVzpbAoGBAKJX1dIrjoLjkqYutKschMrlD2mtjNYsoMu/IfURfTxy72WPlkzO8EnsmUObZAdJ8lWU8v58rfFpW+CeRAOHNxPoorJfegUUnQT8Y3I/vt+/28ujzxVQy6lMzdBOdrRKwGD8TwOOG53v+u84YFwfnX/PJwWGJ/6JRb67U0H/dyvVAoGANJVW/nSAiF4gZqJC6owoq9xrreCqPrVw+pCUOxenqBIJLI3xAtzlZTlXwQz6TDwUe7bpnHoWfEh/Hem+UWAadZGlPY450dOTI8T04p2ukkZkMeQB90+Z4cZW9iahY7uZ1tg4U/5UAAhMri0xF2krS43fpyyTXdiZSHrC0Txj4qI=-----END PRIVATE KEY-----\"\n" +
                "         }\n" +
                "      ]");
        final PartnerConfiguration partnerConfiguration = new PartnerConfiguration(data, sensitiveData);

        final Map<String, PartnerAcquirer> result = underTest.fetchAcquirerList(partnerConfiguration);
        assertNotNull(result);
    }
}
