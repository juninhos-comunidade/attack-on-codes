package com.attackoncodes.worksync.assets.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import com.attackoncodes.worksync.assets.dto.AssetsResponse;
import com.attackoncodes.worksync.assets.model.Assets;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssetsMapper {

	AssetsResponse toResponse(Assets assets);

	@Mapping(source = "file.originalFilename", target = "name")
	@Mapping(source = "file.contentType", target = "contentType")
	@Mapping(source = "file.size", target = "size")
	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void merge(@MappingTarget Assets assets, MultipartFile file, String objectKey, String objectUrl);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void merge(@MappingTarget Assets assets, String objectKey, String objectUrl);

}
