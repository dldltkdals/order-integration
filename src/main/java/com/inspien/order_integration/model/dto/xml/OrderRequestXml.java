package com.inspien.order_integration.model.dto.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "ROOT") // 실제 XML 루트 태그에 맞춰 조정 (제공된 예시엔 생략됨)
public class OrderRequestXml {
    @JacksonXmlProperty(localName = "HEADER")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<HeaderXml> headers;

    @JacksonXmlProperty(localName = "ITEM")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<ItemXml> items;

    @Data
    public static class HeaderXml {
        @JacksonXmlProperty(localName = "USER_ID")
        private String userId;
        @JacksonXmlProperty(localName = "NAME")
        private String name;
        @JacksonXmlProperty(localName = "ADDRESS")
        private String address;
        @JacksonXmlProperty(localName = "STATUS")
        private String status;
    }

    @Data
    public static class ItemXml {
        @JacksonXmlProperty(localName = "USER_ID")
        private String userId;
        @JacksonXmlProperty(localName = "ITEM_ID")
        private String itemId;
        @JacksonXmlProperty(localName = "ITEM_NAME")
        private String itemName;
        @JacksonXmlProperty(localName = "PRICE")
        private String price;
    }
}
