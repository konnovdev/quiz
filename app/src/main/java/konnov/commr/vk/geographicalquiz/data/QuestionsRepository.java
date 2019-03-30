package konnov.commr.vk.geographicalquiz.data;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import konnov.commr.vk.geographicalquiz.data.source.QuestionsDataSource;
import konnov.commr.vk.geographicalquiz.data.pojo.Question;
import konnov.commr.vk.geographicalquiz.data.pojo.Translation;

public class QuestionsRepository implements QuestionsDataSource{

    private static QuestionsRepository INSTANCE = null;

    private final QuestionsDataSource mQuestionsRemoteDataSource;

    private final QuestionsDataSource mQuestionsLocalDataSource;

    SparseArray<Question> mCachedQuestions; //questionId : question map

    SparseArray<Translation> mCachedTranslations; //questionId : translation map

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = true; //TODO mCacheIsDirty = false

    private QuestionsRepository (@NonNull QuestionsDataSource questionsRemoteDataSource,
                                       @NonNull QuestionsDataSource questionsLocalDataSource) {
        mQuestionsRemoteDataSource = questionsRemoteDataSource;
        mQuestionsLocalDataSource = questionsLocalDataSource;
    }


    public static QuestionsRepository getInstance (QuestionsDataSource questionsRemoteDataSource,
                                                   QuestionsDataSource questionsLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new QuestionsRepository(questionsRemoteDataSource, questionsLocalDataSource);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    /**
     * Gets questions from cache, local data source (SQLite) or remote data source, whichever is
     * available first.
     * <p>
     * Note: {@link LoadQuestionsCallback#onDataNotAvailable()} is fired if all data sources fail to
     * get the data.
     */
    @Override
    public void getQuestions(@NonNull LoadQuestionsCallback callback) {
        // Respond immediately with cache if available and not dirty
        if(mCachedQuestions != null && mCachedTranslations != null && !mCacheIsDirty) {
            callback.onQuestionsLoaded(mCachedQuestions);
            callback.onTranslationsLoaded(mCachedTranslations);
            return;
        }

        if(mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getTasksFromRemoteDataSource(callback);
        } else {
            // TODO Query the local storage if available. If not, query the network.
        }
    }



    private void getTasksFromRemoteDataSource(@NonNull final LoadQuestionsCallback callback) {
        mQuestionsRemoteDataSource.getQuestions(new LoadQuestionsCallback() {
            @Override
            public void onQuestionsLoaded(SparseArray<Question> questions) {
                refreshCacheQuestions(questions);
                //TODO refreshLocalDataSource(questions);
                callback.onQuestionsLoaded(questions);
            }

            @Override
            public void onTranslationsLoaded(SparseArray<Translation> translations) {
                refreshCacheTranslations(translations);
                //TODO refreshLocalDataSource(questions);
                callback.onTranslationsLoaded(translations);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCacheQuestions(SparseArray<Question> questions) {
        if (mCachedQuestions == null) {
            mCachedQuestions = new SparseArray<>();
        }
        mCachedQuestions.clear();
        for (int i = 0; i < questions.size(); i++) {
            mCachedQuestions.put(questions.valueAt(i).getQuestionId(), questions.valueAt(i));
        }
        mCacheIsDirty = false;
    }

    private void refreshCacheTranslations(SparseArray<Translation> translations) {
        if (mCachedTranslations == null) {
            mCachedTranslations = new SparseArray<>();
        }
        mCachedTranslations.clear();
        for (int i = 0; i < translations.size(); i++) {
            mCachedTranslations.put(translations.valueAt(i).getQuestionId(), translations.valueAt(i));
        }
        mCacheIsDirty = false;
    }

    @Override
    public void refreshQuestions() {
        mCacheIsDirty = true;
    }
}