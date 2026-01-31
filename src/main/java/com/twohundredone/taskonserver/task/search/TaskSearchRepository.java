package com.twohundredone.taskonserver.task.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface TaskSearchRepository extends ElasticsearchRepository<TaskSearchDocument, Long> {

}
