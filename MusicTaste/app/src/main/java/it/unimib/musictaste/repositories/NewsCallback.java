package it.unimib.musictaste.repositories;

import java.util.List;

import it.unimib.musictaste.utils.News;

public interface NewsCallback {
    void onResponse(List<News> news);
    void onFailure(String msg);
}
