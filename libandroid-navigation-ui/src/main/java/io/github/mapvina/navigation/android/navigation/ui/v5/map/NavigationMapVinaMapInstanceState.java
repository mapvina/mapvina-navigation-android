package io.github.mapvina.navigation.android.navigation.ui.v5.map;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationMapVinaMapInstanceState implements Parcelable {

  private final NavigationMapSettings settings;

  NavigationMapVinaMapInstanceState(NavigationMapSettings settings) {
    this.settings = settings;
  }

  NavigationMapSettings retrieveSettings() {
    return settings;
  }

  private NavigationMapVinaMapInstanceState(Parcel in) {
    settings = in.readParcelable(NavigationMapSettings.class.getClassLoader());
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(settings, flags);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<NavigationMapVinaMapInstanceState> CREATOR =
    new Creator<NavigationMapVinaMapInstanceState>() {
      @Override
      public NavigationMapVinaMapInstanceState createFromParcel(Parcel in) {
        return new NavigationMapVinaMapInstanceState(in);
      }

      @Override
      public NavigationMapVinaMapInstanceState[] newArray(int size) {
        return new NavigationMapVinaMapInstanceState[size];
      }
    };
}
