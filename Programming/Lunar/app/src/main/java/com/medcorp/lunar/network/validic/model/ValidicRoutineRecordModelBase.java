package com.medcorp.lunar.network.validic.model;

/**
 * Created by gaillysu on 16/3/14.
 */
public class ValidicRoutineRecordModelBase {
   String _id;
   String timestamp;
   String utc_offset;
   double steps;
   double distance;
   double floors;
   double elevation;
   double calories_burned;
   String source;
   String source_name;
   String last_updated;
   Boolean validated;
   double water;

 String activity_id;
 RoutineGoal extras;

 public RoutineGoal getExtras() {
  return extras;
 }

 public void setExtras(RoutineGoal extras) {
  this.extras = extras;
 }

 public String getActivity_id() {
  return activity_id;
 }

 public void setActivity_id(String activity_id) {
  this.activity_id = activity_id;
 }

 public String get_id() {
  return _id;
 }

 public void set_id(String _id) {
  this._id = _id;
 }

 public String getTimestamp() {
  return timestamp;
 }

 public void setTimestamp(String timestamp) {
  this.timestamp = timestamp;
 }

 public String getUtc_offset() {
  return utc_offset;
 }

 public void setUtc_offset(String utc_offset) {
  this.utc_offset = utc_offset;
 }

 public double getSteps() {
  return steps;
 }

 public void setSteps(double steps) {
  this.steps = steps;
 }

 public double getDistance() {
  return distance;
 }

 public void setDistance(double distance) {
  this.distance = distance;
 }

 public double getFloors() {
  return floors;
 }

 public void setFloors(double floors) {
  this.floors = floors;
 }

 public double getElevation() {
  return elevation;
 }

 public void setElevation(double elevation) {
  this.elevation = elevation;
 }

 public double getCalories_burned() {
  return calories_burned;
 }

 public void setCalories_burned(double calories_burned) {
  this.calories_burned = calories_burned;
 }

 public String getSource() {
  return source;
 }

 public void setSource(String source) {
  this.source = source;
 }

 public String getSource_name() {
  return source_name;
 }

 public void setSource_name(String source_name) {
  this.source_name = source_name;
 }

 public String getLast_updated() {
  return last_updated;
 }

 public void setLast_updated(String last_updated) {
  this.last_updated = last_updated;
 }

 public Boolean getValidated() {
  return validated;
 }

 public void setValidated(Boolean validated) {
  this.validated = validated;
 }

 public double getWater() {
  return water;
 }

 public void setWater(double water) {
  this.water = water;
 }
}
