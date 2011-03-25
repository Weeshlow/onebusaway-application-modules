package org.onebusaway.api.model.transit.service_alerts;

import java.io.Serializable;
import java.util.List;

public final class SituationAffectsV2Bean implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<SituationAffectedAgencyV2Bean> agencies;

  private List<SituationAffectedStopV2Bean> stops;

  private List<SituationAffectedVehicleJourneyV2Bean> vehicleJourneys;

  public List<SituationAffectedAgencyV2Bean> getAgencies() {
    return agencies;
  }

  public void setAgencies(List<SituationAffectedAgencyV2Bean> agencies) {
    this.agencies = agencies;
  }

  public List<SituationAffectedStopV2Bean> getStops() {
    return stops;
  }

  public void setStops(List<SituationAffectedStopV2Bean> stops) {
    this.stops = stops;
  }

  public List<SituationAffectedVehicleJourneyV2Bean> getVehicleJourneys() {
    return vehicleJourneys;
  }

  public void setVehicleJourneys(
      List<SituationAffectedVehicleJourneyV2Bean> vehicleJourneys) {
    this.vehicleJourneys = vehicleJourneys;
  }
}