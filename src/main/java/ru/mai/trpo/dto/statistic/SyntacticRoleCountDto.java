package ru.mai.trpo.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SyntacticRoleCountDto {
    private String syntacticRoleDescription;
    private Long count;
}
