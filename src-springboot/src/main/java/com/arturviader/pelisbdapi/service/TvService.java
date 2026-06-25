package com.arturviader.pelisbdapi.service;

import com.arturviader.pelisbdapi.dto.MovieTMDB;
import com.arturviader.pelisbdapi.dto.SerieTMDB;
import com.arturviader.pelisbdapi.model.Movie;
import com.arturviader.pelisbdapi.model.Serie;
import com.arturviader.pelisbdapi.repository.MovieRepository;
import com.arturviader.pelisbdapi.repository.TvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class TvService {
    @Autowired
    private TvRepository tvRepository;

    public Serie saveIfNotExists(SerieTMDB tmdbSerie, String mediaType) {
        return tvRepository.findByTmdbId(tmdbSerie.getId())
                .map(tv -> {
                    if (needsUpdate(tv, tmdbSerie)) {
                        updateSerie(tv, tmdbSerie);
                        return tvRepository.save(tv);
                    }
                    return tv;
                })
                .orElseGet(() -> {
                    Serie tv = new Serie();
                    tv.setTmdbId(tmdbSerie.getId());
                    tv.setTitle(tmdbSerie.getName());
                    tv.setPosterPath(tmdbSerie.getPosterPath());
                    tv.setReleaseDate(LocalDate.parse(tmdbSerie.getFirstAirDate()));
                    return tvRepository.save(tv);
                });
    }

    private boolean needsUpdate(Serie serie, SerieTMDB tmdbSerie) {
        return !serie.getTitle().equals(tmdbSerie.getName())
                || !serie.getPosterPath().equals(tmdbSerie.getPosterPath());
    }

    private void updateSerie(Serie serie, SerieTMDB tmdbSerie) {
        serie.setTitle(tmdbSerie.getName());
        serie.setPosterPath(tmdbSerie.getPosterPath());
    }

    public Optional<Serie> findById(Long tmdbId) {
        return tvRepository.findByTmdbId(tmdbId);
    }

}
