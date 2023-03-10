package site.devroad.softeer.src.roadmap.dto;

import site.devroad.softeer.src.roadmap.dto.domain.ChapterDetail;

public class GetChapterDetailRes {
    private boolean success;
    private ChapterDetail chapterDetail;

    public GetChapterDetailRes(ChapterDetail chapterDetail) {
        this.success = true;
        this.chapterDetail = chapterDetail;
    }

    public boolean isSuccess() {
        return success;
    }

    public ChapterDetail getChapterDetail() {
        return chapterDetail;
    }
}
