package com.cotiviti.datainit.config;

import com.cotiviti.dto.RoleGroupMapYml;
import com.cotiviti.utils.YamlPropertySourceFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "data-init")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:roleGroupMap.yml")
public class RoleGroupMapInitConfiguration {

    private List<RoleGroupMapYml> RoleGroupMapList;
}
