package com.mall.beans;

import lombok.*;

import java.util.Set;

/**
 * Created by 王乾 on 2018/1/19.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {

    private String subject;

    private String message;

    private Set<String> receivers;
}
