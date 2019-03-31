package konnov.commr.vk.geographicalquiz.data.source;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import konnov.commr.vk.geographicalquiz.data.pojo.Question;
import konnov.commr.vk.geographicalquiz.data.pojo.Translation;

public interface QuestionsDataSource {

    interface LoadQuestionsCallback {

        void onQuestionsLoaded(SparseArray<Question> questions);

        void onTranslationsLoaded(SparseArray<Translation> translations);

        void onDataNotAvailable();
    }

    interface GetQuestionsCallback {

        void onQuestionsLoaded(SparseArray<Question> questions);

        void onTranslationsLoaded(SparseArray<Translation> translations);

        void onDataNotAvailable();
    }

    void getQuestions(@NonNull LoadQuestionsCallback callback);

    void saveQuestions(@NonNull SparseArray<Question> questions);

    void saveTranslation(@NonNull SparseArray<Translation> translations);

    void refreshQuestions();

    void deleteAllQuestions();

}
