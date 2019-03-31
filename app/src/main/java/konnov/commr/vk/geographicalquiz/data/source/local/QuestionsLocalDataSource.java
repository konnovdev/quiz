package konnov.commr.vk.geographicalquiz.data.source.local;

import android.util.SparseArray;

import java.util.List;

import androidx.annotation.NonNull;
import konnov.commr.vk.geographicalquiz.data.pojo.Question;
import konnov.commr.vk.geographicalquiz.data.pojo.Translation;
import konnov.commr.vk.geographicalquiz.data.source.QuestionsDataSource;
import konnov.commr.vk.geographicalquiz.util.AppExecutors;
import konnov.commr.vk.geographicalquiz.util.Misc;

public class QuestionsLocalDataSource implements QuestionsDataSource {

    private static QuestionsLocalDataSource INSTANCE;

    private QuestionsDao mQuestionsDao;

    private TranslationsDao mTranslationsDao;

    private AppExecutors mAppExecutors;


    private QuestionsLocalDataSource(@NonNull AppExecutors appExecutors,
                                     @NonNull QuestionsDao questionsDao,
                                     @NonNull TranslationsDao translationsDao){
        mAppExecutors = appExecutors;
        mQuestionsDao = questionsDao;
        mTranslationsDao = translationsDao;
    }

    public static QuestionsLocalDataSource getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull QuestionsDao questionsDao,
                                                       @NonNull TranslationsDao translationsDao) {
        if(INSTANCE == null) {
            INSTANCE = new QuestionsLocalDataSource(appExecutors, questionsDao, translationsDao);
        }
        return INSTANCE;
    }

    @Override
    public void getQuestions(@NonNull final LoadQuestionsCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Translation> translationsList = mTranslationsDao.getTranslations();
                final List<Question> questionsList = mQuestionsDao.getQuestions();

                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if(translationsList != null && questionsList != null) {
                            callback.onQuestionsLoaded(Misc.questionListToSparseArray(questionsList));
                            callback.onTranslationsLoaded(Misc.translationListToSparseArray(translationsList));
                        }
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveQuestions(@NonNull final SparseArray<Question> questions) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < questions.size(); i++) {
                    mQuestionsDao.insertQuestion(questions.valueAt(i));
                }
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void saveTranslation(@NonNull final SparseArray<Translation> translations) {
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < translations.size(); i++) {
                    mTranslationsDao.insertTranslation(translations.valueAt(i));
                }
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void refreshQuestions() {
        // Not required because the {@link TasksRepository} handles the logic of refreshing the
        // tasks from all the available data sources.
    }

    @Override
    public void deleteAllQuestions() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mQuestionsDao.deleteQuestions();
                mTranslationsDao.deleteTranslations();
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }
}
