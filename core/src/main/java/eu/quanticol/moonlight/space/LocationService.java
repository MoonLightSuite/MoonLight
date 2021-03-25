/**
 * 
 */
package eu.quanticol.moonlight.space;

import java.util.Iterator;

import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.util.Pair;

/**
 * @author loreti
 *
 */
public interface LocationService<T, V> {
	
	SpatialModel<V> get(T t);
	
	Iterator<Pair<T, SpatialModel<V>>> times();

	boolean isEmpty();

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyMatrix(int locations, RecordHandler edgeRecordHandler, double[] locationTimeArray,
                                                                                    String[][][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		for( int i=0 ; i<locationTimeArray.length ; i++ ) {
			toReturn.add(locationTimeArray[i], SpatialModel.buildSpatialModelFromAdjacencyMatrix(locations,edgeRecordHandler,graph[i]));
		}
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyMatrix(int locations, RecordHandler edgeRecordHandler, double time,
                                                                                    String[][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		toReturn.add(time, SpatialModel.buildSpatialModelFromAdjacencyMatrix(locations,edgeRecordHandler,graph));
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyMatrix(int locations, RecordHandler edgeRecordHandler, double[] locationTimeArray,
                                                                                    double[][][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		for( int i=0 ; i<locationTimeArray.length ; i++ ) {
			toReturn.add(locationTimeArray[i], SpatialModel.buildSpatialModelFromAdjacencyMatrix(locations,edgeRecordHandler,graph[i]));
		}
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyMatrix(int locations, RecordHandler edgeRecordHandler, double time,
                                                                                    double[][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		toReturn.add(time, SpatialModel.buildSpatialModelFromAdjacencyMatrix(locations,edgeRecordHandler,graph));
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyList(int locations, RecordHandler edgeRecordHandler, double[] locationTimeArray,
                                                                                  String[][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		for( int i=0 ; i<locationTimeArray.length ; i++ ) {
			toReturn.add(locationTimeArray[i], SpatialModel.buildSpatialModelFromAdjacencyList(locations,edgeRecordHandler,graph[i]));
		}
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyList(int locations, RecordHandler edgeRecordHandler, double time,
                                                                                  String[][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		toReturn.add(time, SpatialModel.buildSpatialModelFromAdjacencyList(locations,edgeRecordHandler,graph));
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyList(int locations, RecordHandler edgeRecordHandler, double[] locationTimeArray,
                                                                                  double[][][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		for( int i=0 ; i<locationTimeArray.length ; i++ ) {
			toReturn.add(locationTimeArray[i], SpatialModel.buildSpatialModelFromAdjacencyList(locations,edgeRecordHandler,graph[i]));
		}
		return toReturn;
	}

	static LocationService<Double, MoonLightRecord> buildLocationServiceFromAdjacencyList(int locations, RecordHandler edgeRecordHandler, double time,
                                                                                  double[][] graph) {
		LocationServiceList<MoonLightRecord> toReturn = new LocationServiceList<>();
		toReturn.add(time, SpatialModel.buildSpatialModelFromAdjacencyList(locations,edgeRecordHandler,graph));
		return toReturn;
	}
}
