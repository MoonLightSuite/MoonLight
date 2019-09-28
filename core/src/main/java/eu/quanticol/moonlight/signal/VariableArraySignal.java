/*******************************************************************************
 * MoonLight: a light-weight framework for runtime monitoring
 * Copyright (C) 2018 
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package eu.quanticol.moonlight.signal;

import java.util.HashMap;

/**
 * @author loreti
 *
 */
public class VariableArraySignal extends Signal<Assignment>{
	
	private AssignmentFactory factory;
	private final HashMap<String,Integer> vTable = new HashMap<>();
	
	public VariableArraySignal(String[] array, AssignmentFactory factory) {
		this.factory = factory;
		for( int i=0 ; i<array.length; i++ ) {
			if (vTable.put(array[i], i) != null) {
				throw new IllegalArgumentException("Duplicated variable "+array[i]);
			}
		}
	}
	
	public void add( double t , Object ... values ) {
		add(t, factory.get(values));
	}

	public int getVariableIndex(String name) {
		return vTable.getOrDefault(name, -1);
	}
	

}