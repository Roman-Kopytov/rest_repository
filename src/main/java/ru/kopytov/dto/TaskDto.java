package ru.kopytov.dto;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDto {

    private long id;
    private String title;
    private String description;
    private long userId;
}
