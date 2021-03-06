package konnov.commr.vk.geographicalquiz.data.source.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import konnov.commr.vk.geographicalquiz.data.Entries;
import konnov.commr.vk.geographicalquiz.data.pojo.Question;
import konnov.commr.vk.geographicalquiz.data.pojo.Translation;

@Database(entities = {Question.class, Translation.class}, version = Entries.DB_VERSION, exportSchema = false)
public abstract class QuestionsDatabase extends RoomDatabase {

    private static QuestionsDatabase INSTANCE;

    public abstract QuestionsDao questionsDao();
    public abstract TranslationsDao translationsDao();

    private static final Object sLock = new Object();

    public static QuestionsDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        QuestionsDatabase.class, Entries.DB_NAME)
                        .build();
            }
            return INSTANCE;
        }
    }

}
