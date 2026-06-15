package com.arturviader.pelisbdapi.dto;

import java.util.List;

public record VideosResponseDTO (String id,List<VideoTMDB> results) {

}
