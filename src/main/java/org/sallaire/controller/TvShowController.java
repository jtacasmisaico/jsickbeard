package org.sallaire.controller;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.sallaire.dto.api.FullShow;
import org.sallaire.dto.api.TvShowConfigurationParam;
import org.sallaire.dto.api.UpdateEpisodeStatusParam;
import org.sallaire.dto.metadata.TvShow;
import org.sallaire.dto.user.EpisodeStatus;
import org.sallaire.service.DownloadService;
import org.sallaire.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TvShowController {

	@Autowired
	private ShowService showService;

	@Autowired
	private DownloadService downloadService;

	@RequestMapping(value = "/tvshow/{id}", method = RequestMethod.POST)
	public void addShow(@PathVariable("id") Long id, @RequestBody TvShowConfigurationParam showConfig) {
		showService.add(id, showConfig);
	}

	@RequestMapping(value = "/tvshow/{id}", method = RequestMethod.PUT)
	public void updateShow(@PathVariable("id") Long id, @RequestParam(value = "location", required = false) String location, @RequestParam(value = "quality", required = false) String quality, @RequestParam(value = "audio", required = false) String audioLang, @RequestParam(value = "customName", required = false) List<String> customNames) {
		showService.update(id, location, quality, audioLang, customNames);
	}

	@RequestMapping(value = "/tvshow/{id}", method = RequestMethod.GET)
	public ResponseEntity<FullShow> getFullShow(@PathVariable("id") Long id) {
		FullShow result = showService.getFullShow(id);
		if (result != null) {
			return ResponseEntity.ok(result);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@RequestMapping(value = "/tvshow/{id}", method = RequestMethod.DELETE)
	public void unFollowShow(@PathVariable("id") Long id) {
		showService.unFollowShow(id);
	}

	@RequestMapping(value = "/tvshows", method = RequestMethod.GET)
	public Collection<TvShow> getShows() {
		return showService.getShows();
	}

	@RequestMapping(value = "/tvshow/{id}/episodes", method = RequestMethod.PUT)
	public ResponseEntity<String> updateEpisodesStatus(@PathVariable("id") Long id, @RequestBody UpdateEpisodeStatusParam params) {
		if (params.getStatus() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required String parameter 'status' is not present");
		}
		if (CollectionUtils.isEmpty(params.getIds())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Required List<Long> parameter 'ids' is not present");
		}
		showService.updateEpisodesStatus(id, params);
		return ResponseEntity.ok("");
	}

	@RequestMapping(value = "/tvshow/{id}/episode/{epId}", method = RequestMethod.POST)
	public void searchEpisode(@PathVariable("id") Long id, @PathVariable("epId") Long episodeId) {
		showService.searchEpisode(id, episodeId);
	}

	@RequestMapping(value = "/tvshow/{id}/episode/{epId}", method = RequestMethod.DELETE)
	public void truncateDownloadedEpisode(@PathVariable("id") Long id, @PathVariable("epId") Long episodeId) {
		showService.truncateDownloadedEpisode(id, episodeId);
	}

	@RequestMapping(value = "/tvshow/{id}/episode/{season}/{episode}/status", method = RequestMethod.GET)
	public EpisodeStatus getEpisodeStatus(@PathVariable("id") Long id, @PathVariable("season") Integer season, @PathVariable("episode") Integer episode, @RequestParam("quality") String quality, @RequestParam("lang") String lang) {
		return downloadService.getEpisodeStatus(id, season, episode, quality, lang);
	}

	@RequestMapping(value = "/tvshow/episodes/status", method = RequestMethod.GET)
	public Collection<EpisodeStatus> getEpisodeStatus() {
		return downloadService.getEpisodeStatus();
	}
}
