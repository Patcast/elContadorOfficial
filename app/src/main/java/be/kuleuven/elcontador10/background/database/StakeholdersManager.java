package be.kuleuven.elcontador10.background.database;

import java.util.ArrayList;

import be.kuleuven.elcontador10.background.interfaces.stakeholders.StakeholdersSummaryInterface;
import be.kuleuven.elcontador10.background.parcels.FilterStakeholdersParcel;

public class StakeholdersManager {

    private static volatile  StakeholdersManager INSTANCE = null;

    private final ArrayList<String> titles;
    private final ArrayList<String> descriptions;
    private final ArrayList<String> status;
    private final ArrayList<String> metadata;

    private StakeholdersManager() {
        titles = new ArrayList<>();
        descriptions = new ArrayList<>();
        status = new ArrayList<>();
        metadata = new ArrayList<>();
    }

    public static StakeholdersManager getInstance() {
        if (INSTANCE == null) {
            synchronized (StakeholdersManager.class) {
                if (INSTANCE == null) INSTANCE = new StakeholdersManager();
            }
        }

        return INSTANCE;
    }

    public void getStakeholders(StakeholdersSummaryInterface summaryInterface, FilterStakeholdersParcel parcel) {

    }
}
