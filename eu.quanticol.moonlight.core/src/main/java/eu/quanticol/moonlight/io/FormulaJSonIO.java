/**
 * 
 */
package eu.quanticol.moonlight.io;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;

import eu.quanticol.moonlight.formula.AndFormula;
import eu.quanticol.moonlight.formula.AtomicFormula;
import eu.quanticol.moonlight.formula.EventuallyFormula;
import eu.quanticol.moonlight.formula.Formula;
import eu.quanticol.moonlight.formula.FormulaVisitor;
import eu.quanticol.moonlight.formula.GloballyFormula;
import eu.quanticol.moonlight.formula.Interval;
import eu.quanticol.moonlight.formula.NegationFormula;
import eu.quanticol.moonlight.formula.OrFormula;
import eu.quanticol.moonlight.formula.UntilFormula;

/**
 * @author loreti
 *
 */
public class FormulaJSonIO {
	
	private static final String TYPE_KEY = "formula";
	private static final String ATOMIC_ID = "id";
	private static final String FIRST_ARGUMENT = "first";
	private static final String SECOND_ARGUMENT = "second";
	private static final String ARGUMENT = "arg";
	private static final String LOWER_KEY = "lower";
	private static final String UPPER_KEY = "lower";
	private static final String UNBOUNDED_KEY = "unbound";
		
	
	private static FormulaJSonIO jsonIo;
	private GsonBuilder gson;
	
	private FormulaJSonIO() {
		this.gson = new GsonBuilder();
		this.gson.registerTypeHierarchyAdapter(Formula.class, new FormulaSerialiserDeserialiser());
	}
	
	
	public class FormulaSerialiser implements JsonSerializer<Formula>, FormulaVisitor<JsonSerializationContext, JsonElement> {

		@Override
		public JsonElement serialize(Formula src, Type typeOfSrc, JsonSerializationContext context) {
			return src.accept(this, context);
		}

		@Override
		public JsonElement visit(AtomicFormula atomicFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.ATOMIC.name());
			o.addProperty(ATOMIC_ID, atomicFormula.getAtomicId());
			return o;
		}

		@Override
		public JsonElement visit(AndFormula andFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.AND.name());
			o.add(FIRST_ARGUMENT, andFormula.getFirstArgument().accept(this, parameters));
			o.add(SECOND_ARGUMENT, andFormula.getSecondArgument().accept(this, parameters));
			return o;
		}

		@Override
		public JsonElement visit(NegationFormula negationFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.NOT.name());
			o.add(ARGUMENT, negationFormula.accept(this, parameters));
			return o;
		}

		@Override
		public JsonElement visit(OrFormula orFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.OR.name());
			o.add(FIRST_ARGUMENT, orFormula.getFirstArgument().accept(this, parameters));
			o.add(SECOND_ARGUMENT, orFormula.getSecondArgument().accept(this, parameters));
			return o;
		}

		@Override
		public JsonElement visit(EventuallyFormula eventuallyFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.EVENTUALLY.name());
			Interval i = eventuallyFormula.getInterval();
			o.addProperty(LOWER_KEY, i.getStart());
			o.addProperty(UPPER_KEY, i.getEnd());
			o.add(ARGUMENT, eventuallyFormula.getArgument().accept(this, parameters));
			return o;
		}

		@Override
		public JsonElement visit(GloballyFormula globallyFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.GLOBALLY.name());
			Interval i = globallyFormula.getInterval();
			o.addProperty(LOWER_KEY, i.getStart());
			o.addProperty(UPPER_KEY, i.getEnd());
			o.add(ARGUMENT, globallyFormula.getArgument().accept(this, parameters));
			return o;
		}

		@Override
		public JsonElement visit(UntilFormula untilFormula, JsonSerializationContext parameters) {
			JsonObject o = new JsonObject();
			o.addProperty(TYPE_KEY, FormulaType.UNTIL.name());
			if (untilFormula.isUnbounded()) {
				o.addProperty(UNBOUNDED_KEY, true);
			} else {
				Interval i = untilFormula.getInterval();
				o.addProperty(LOWER_KEY, i.getStart());
				o.addProperty(UPPER_KEY, i.getEnd());
			}
			o.add(FIRST_ARGUMENT, untilFormula.getFirstArgument().accept(this, parameters));
			o.add(SECOND_ARGUMENT, untilFormula.getFirstArgument().accept(this, parameters));
			return o;
		}

	}
	
	public class FormulaSerialiserDeserialiser implements JsonDeserializer<Formula> {

		@Override
		public Formula deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			if (json.isJsonObject()) {
				return deserializeFormula(json.getAsJsonObject(),context);
			} else {
				return null;
			}
		}

		private Formula deserializeFormula(JsonObject json, JsonDeserializationContext context) {
			switch (FormulaType.valueOf(json.get(TYPE_KEY).getAsString())) {
			case AND:
				return new AndFormula(
					deserializeFormula(json.get(FIRST_ARGUMENT).getAsJsonObject(),context), 
					deserializeFormula(json.get(SECOND_ARGUMENT).getAsJsonObject(),context) 
				);
			case ATOMIC:
				return new AtomicFormula(json.get(ATOMIC_ID).getAsString());
			case EVENTUALLY:
				return new EventuallyFormula(
					deserializeFormula(json.get(ARGUMENT).getAsJsonObject(), context), 
					new Interval(json.get(LOWER_KEY).getAsDouble(), json.get(UPPER_KEY).getAsDouble())
				);
			case GLOBALLY:
				return new GloballyFormula(
						deserializeFormula(json.get(ARGUMENT).getAsJsonObject(), context), 
						new Interval(json.get(LOWER_KEY).getAsDouble(), json.get(UPPER_KEY).getAsDouble())
					);
			case NOT:
				return new NegationFormula(
						deserializeFormula(json.get(ARGUMENT).getAsJsonObject(), context)
					);
			case OR:
				return new OrFormula(
						deserializeFormula(json.get(FIRST_ARGUMENT).getAsJsonObject(),context), 
						deserializeFormula(json.get(SECOND_ARGUMENT).getAsJsonObject(),context) 
					);
			case UNTIL:
				Formula first = deserializeFormula(json.get(FIRST_ARGUMENT).getAsJsonObject(),context);
				Formula second = deserializeFormula(json.get(SECOND_ARGUMENT).getAsJsonObject(),context);
				if (json.has(UNBOUNDED_KEY)) {
					return new UntilFormula(first, second);
				} else {
					return new UntilFormula(first, second, new Interval(json.get(LOWER_KEY).getAsDouble(), json.get(UPPER_KEY).getAsDouble()));
				}
			default:
				return null;
			}
		}

		
	}

}
