package com.example.studysecurity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GoogleAccountProfileResponse {
    private String email;
    private String name;
    // 필요 시 profileImage, locale 등 추가
}
