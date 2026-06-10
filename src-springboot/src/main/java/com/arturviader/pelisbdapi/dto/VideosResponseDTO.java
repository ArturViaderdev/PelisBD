package com.arturviader.pelisbdapi.dto;

import com.arturviader.pelisbdapi.model.VideoTMDB;

import java.util.List;

public record VideosResponseDTO (String id,List<VideoTMDB> results) {

}
