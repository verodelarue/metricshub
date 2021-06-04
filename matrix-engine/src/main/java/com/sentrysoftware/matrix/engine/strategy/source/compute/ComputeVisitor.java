package com.sentrysoftware.matrix.engine.strategy.source.compute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.sentrysoftware.matrix.common.helpers.HardwareConstants;
import com.sentrysoftware.matrix.common.helpers.TriFunction;
import com.sentrysoftware.matrix.connector.model.Connector;
import com.sentrysoftware.matrix.connector.model.common.TranslationTable;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.AbstractConcat;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.AbstractMatchingLines;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Add;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.And;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.ArrayTranslate;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Awk;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Compute;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Convert;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Divide;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.DuplicateColumn;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.ExcludeMatchingLines;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Extract;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.ExtractPropertyFromWbemPath;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Json2CSV;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.KeepColumns;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.KeepOnlyMatchingLines;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.LeftConcat;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Multiply;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.PerBitTranslation;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Replace;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.RightConcat;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Substract;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Substring;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.Translate;
import com.sentrysoftware.matrix.connector.model.monitor.job.source.compute.XML2CSV;
import com.sentrysoftware.matrix.engine.strategy.source.SourceTable;
import com.sentrysoftware.matrix.engine.strategy.utils.PslUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ComputeVisitor implements IComputeVisitor {

	private static final Pattern COLUMN_PATTERN =  Pattern.compile(HardwareConstants.COLUMN_REGEXP, Pattern.CASE_INSENSITIVE);

	@Getter
	@Setter
	private SourceTable sourceTable;

	@Setter
	private Connector connector;

	private static final TriFunction<List<String>, Integer, String, Integer> GET_VALUE_FROM_ROW = (row, index, value) -> {
		if (index < row.size()) {
			return transformToIntegerValue(row.get(index));
		}
		log.warn("Cannot get value at index {} from the row {}", index, row);
		return null;
	};

	private static final TriFunction<List<String>, Integer, String, Integer> GET_VALUE = (row, index, value) -> transformToIntegerValue(value);

	private static final Map<Class<? extends Compute>, BiFunction<String, String, String>> MATH_FUNCTIONS_MAP;

	private static final BiFunction<String, Map<String, String>, String> PER_BIT_MATCHES_TRANSLATION_FUNCTION = (str, translations) -> translations.get(
			HardwareConstants.OPENING_PARENTHESIS + str + HardwareConstants.COMMA + HardwareConstants.ONE + HardwareConstants.CLOSING_PARENTHESIS);

	private static final BiFunction<String, Map<String, String>, String> PER_BIT_NOT_MATCHES_TRANSLATION_FUNCTION = (str, translations) -> translations.get(
			HardwareConstants.OPENING_PARENTHESIS + str + HardwareConstants.COMMA + HardwareConstants.ZERO +  HardwareConstants.CLOSING_PARENTHESIS);

	private static final BiFunction<String, Map<String, String>, String> TRANSLATION_FUNCTION = (str, translations) -> translations.get(str);

	static {
		MATH_FUNCTIONS_MAP = Map.of(
			Add.class, (op1, op2) -> Double.toString(Double.parseDouble(op1) + Double.parseDouble(op2)),
			Substract.class, (op1, op2) -> Double.toString(Double.parseDouble(op1) - Double.parseDouble(op2)),
			Multiply.class, (op1, op2) -> Double.toString(Double.parseDouble(op1) * Double.parseDouble(op2)),
			Divide.class, (op1, op2) -> {
				Double op2Value = Double.parseDouble(op2);
				if (op2Value != 0) {
					return Double.toString(Double.parseDouble(op1) / op2Value);
				}
				return null;
			});
	}

	@Override
	public void visit(final Add add) {
		if (add == null) {
			log.warn("Compute Operation (Add) is null, the table remains unchanged.");
			return;
		}

		Integer columnIndex = add.getColumn();
		String operand2 = add.getAdd();

		if (columnIndex == null || operand2 == null ) {
			log.warn("Arguments in Compute Operation (Add) : {} are wrong, the table remains unchanged.", add);
			return;
		}

		if (columnIndex < 1 ) {
			log.warn("The index of the column to add cannot be < 1, the addition computation cannot be performed.");
			return;
		}

		performMathematicalOperation(add, columnIndex, operand2);
	}

	@Override
	public void visit(final ArrayTranslate arrayTranslate) {
		// Not implemented yet
	}

	@Override
	public void visit(final And and) {
		if (and == null) {
			log.warn("Compute Operation (And) is null, the table remains unchanged.");
			return;
		}

		Integer columnIndex = and.getColumn() - 1;
		String operand2 = and.getAnd();

		if (columnIndex == null || operand2 == null) {
			log.warn("Arguments in Compute Operation (And) : {} are wrong, the table remains unchanged.", and);
			return;
		}

		if (columnIndex < 0) {
			log.warn("The index of the column to which apply the And operation cannot be < 1, the And computation cannot be performed.");
			return;
		}

		int colOperand2 = getColumnIndex(operand2);

		try {
			sourceTable.getTable()
			.forEach(line -> line.set(columnIndex, String.valueOf(Integer.parseInt(line.get(columnIndex))
					& (colOperand2 == -1 ? Integer.parseInt(operand2) : Integer.parseInt(line.get(colOperand2)))
					)));
		} catch (NumberFormatException e) {
			log.warn("Data is not correctly formatted.");
			return;
		}
	}

	@Override
	public void visit(final Awk awk) {
		// Not implemented yet
	}

	@Override
	public void visit(final Convert convert) {
		// Not implemented yet
	}

	@Override
	public void visit(final Divide divide) {
		if (divide == null) {
			log.warn("Compute Operation (Divide) is null, the table remains unchanged.");
			return;
		}

		if (divide.getColumn() == null || divide.getDivideBy() == null) {
			log.warn("Arguments in Compute Operation (Divide) : {} are wrong, the table remains unchanged.", divide);
			return;
		}

		Integer columnIndex = divide.getColumn();
		String divideBy = divide.getDivideBy();

		if (columnIndex < 1) {
			log.warn("The index of the column to divide cannot be < 1, the division computation cannot be performed.");
			return;
		}

		performMathematicalOperation(divide, columnIndex, divideBy);

	}

	@Override
	public void visit(final DuplicateColumn duplicateColumn) {

		if (duplicateColumn == null) {
			log.debug("DuplicateColumn object is null, the table remains unchanged.");
			return;
		}

		if (duplicateColumn.getColumn() == null || duplicateColumn.getColumn() == 0) {
			log.debug("The column index in DuplicateColumn cannot be null or 0, the table remains unchanged.");
			return;
		}

		// for each list in the list, duplicate the column of the given index  
		int columnIndex = duplicateColumn.getColumn() -1;

		for (List<String> elementList : sourceTable.getTable()) {
			if (columnIndex >= 0 && columnIndex < elementList.size()) {
				elementList.add(columnIndex, elementList.get(columnIndex));
			}
		}

	}

	@Override
	public void visit(final ExcludeMatchingLines excludeMatchingLines) {

		processAbstractMatchingLines(excludeMatchingLines);
	}

	@Override
	public void visit(final Extract extract) {
		// Not implemented yet
	}

	@Override
	public void visit(final ExtractPropertyFromWbemPath extractPropertyFromWbemPath) {
		// Not implemented yet
	}

	@Override
	public void visit(final Json2CSV json2csv) {
		// Not implemented yet
	}

	@Override
	public void visit(final KeepColumns keepColumns) {

		if (keepColumns == null) {
			log.warn("KeepColumns object is null, the table remains unchanged.");
			return;
		}

		if (keepColumns.getColumnNumbers() == null || keepColumns.getColumnNumbers().isEmpty()) {
			log.warn("The column number list in KeepColumns cannot be null or empty. The table remains unchanged.");
			return;
		}

		List<List<String>> resultTable = new ArrayList<>();
		List<String> resultRow;
		for (List<String> row : sourceTable.getTable()) {

			resultRow = new ArrayList<>();
			for (Integer columnIndex : keepColumns.getColumnNumbers()) {

				if (columnIndex == null || columnIndex < 1 || columnIndex > row.size()) {

					log.warn("Invalid index for a {}-sized row: {}. The table remains unchanged.",
						row.size(), columnIndex);

					return;
				}

				resultRow.add(row.get(columnIndex - 1));
			}

			resultTable.add(resultRow);
		}

		sourceTable.setTable(resultTable);
	}

	@Override
	public void visit(final KeepOnlyMatchingLines keepOnlyMatchingLines) {

		processAbstractMatchingLines(keepOnlyMatchingLines);
	}

	/**
	 * Updates the {@link SourceTable}
	 * by keeping or removing lines
	 * according to the definition of the given {@link AbstractMatchingLines}.
	 *
	 * @param abstractMatchingLines	The {@link AbstractMatchingLines}
	 *                              describing the rules
	 *                              regarding which lines should be kept or removed in/from the {@link SourceTable}.
	 */
	private void processAbstractMatchingLines(AbstractMatchingLines abstractMatchingLines) {

		if (abstractMatchingLines != null
				&& abstractMatchingLines.getColumn() != null
				&& abstractMatchingLines.getColumn() > 0
				&& sourceTable != null
				&& sourceTable.getTable() != null
				&& !sourceTable.getTable().isEmpty()
				&& abstractMatchingLines.getColumn() <= sourceTable.getTable().get(0).size()) {

			int columnIndex = abstractMatchingLines.getColumn() - 1;

			String pslRegexp = abstractMatchingLines.getRegExp();
			List<String> valueList = abstractMatchingLines.getValueList();

			List<List<String>> table = sourceTable.getTable();

			// If there are both a regex and a valueList, both are applied, one after the other.
			if (pslRegexp != null && !pslRegexp.isEmpty()) {

				table = filterTable(table, columnIndex, getPredicate(pslRegexp, abstractMatchingLines));
			}

			if (valueList != null && !valueList.isEmpty()) {

				table = filterTable(table, columnIndex, getPredicate(valueList, abstractMatchingLines));
			}

			sourceTable.setTable(table);
		}
	}

	/**
	 * @param pslRegexp				The PSL regular expression used to filter the lines in the {@link SourceTable}.
	 * @param abstractMatchingLines	The {@link AbstractMatchingLines}
	 *                              describing the rules
	 *                              regarding which lines should be kept or removed in/from the {@link SourceTable}.
	 *
	 * @return						A {@link Predicate},
	 * 								based on the given regular expression
	 * 								and the concrete type of the given {@link AbstractMatchingLines},
	 * 								that can be used to filter the lines in the {@link SourceTable}.
	 */
	private Predicate<String> getPredicate(String pslRegexp, AbstractMatchingLines abstractMatchingLines) {

		Pattern pattern = Pattern.compile(PslUtils.psl2JavaRegex(pslRegexp));

		return abstractMatchingLines instanceof KeepOnlyMatchingLines
			? value -> pattern.matcher(value).matches()
			: value -> !pattern.matcher(value).matches();
	}

	/**
	 * @param valueList				The list of values used to filter the lines in the {@link SourceTable}.
	 * @param abstractMatchingLines	The {@link AbstractMatchingLines}
	 *                              describing the rules
	 *                              regarding which lines should be kept or removed in/from the {@link SourceTable}.
	 *
	 * @return						A {@link Predicate},
	 * 								based on the given list of values
	 * 								and the concrete type of the given {@link AbstractMatchingLines},
	 * 								that can be used to filter the lines in the {@link SourceTable}.
	 */
	private Predicate<String> getPredicate(List<String> valueList, AbstractMatchingLines abstractMatchingLines) {

		return abstractMatchingLines instanceof KeepOnlyMatchingLines
			? valueList::contains
			: value -> !valueList.contains(value);
	}

	/**
	 * @param table			The table that is being filtered.
	 * @param columnIndex	The index of the column
	 *                      whose values should evaluate to true against the given {@link Predicate}.
	 * @param predicate		The {@link Predicate} against which
	 *                      each value at the given column in the resulting table
	 *                      must evaluate to true.
	 *
	 * @return				A new table
	 * 						having just the rows of the given table
	 * 						for which values at the given column evaluate to true against the given {@link Predicate}.
	 */
	private List<List<String>> filterTable(List<List<String>> table, int columnIndex, Predicate<String> predicate) {

		List<List<String>> sourceTableTmp = new ArrayList<>();
		for (List<String> line : table) {

			if (predicate.test(line.get(columnIndex))) {
				sourceTableTmp.add(line);
			}
		}

		return sourceTableTmp;
	}

	@Override
	public void visit(final LeftConcat leftConcat) {

		processAbstractConcat(leftConcat);
	}

	private void processAbstractConcat(AbstractConcat abstractConcat) {

		if (abstractConcat != null
			&& abstractConcat.getString() != null
			&& abstractConcat.getColumn() != null
			&& abstractConcat.getColumn() > 0
			&& sourceTable != null
			&& sourceTable.getTable() != null
			&& !sourceTable.getTable().isEmpty()
			&& abstractConcat.getColumn() <= sourceTable.getTable().get(0).size()) {

			int columnIndex = abstractConcat.getColumn() - 1;
			String concatString = abstractConcat.getString();

			// If abstractConcat.getString() is like "Column(n)",
			// we concat the column n instead of abstractConcat.getString()
			Matcher matcher = COLUMN_PATTERN.matcher(concatString);
			if (matcher.matches()) {

				int concatColumnIndex = Integer.parseInt(matcher.group(1)) - 1;
				if (concatColumnIndex < sourceTable.getTable().get(0).size()) {

					sourceTable.getTable()
						.forEach(line -> concatColumns(line, columnIndex, concatColumnIndex, abstractConcat));
				}

			} else {

				sourceTable.getTable()
					.forEach(line -> concatString(line, columnIndex, abstractConcat));

				// Serialize and deserialize
				// in case the String to concat contains a ';'
				// so that a new column is created.
				if (concatString.contains(HardwareConstants.SEMICOLON)) {

					sourceTable.setTable(
						SourceTable.csvToTable(
							SourceTable.tableToCsv(sourceTable.getTable(), HardwareConstants.SEMICOLON),
							HardwareConstants.SEMICOLON));
				}
			}
		}
	}

	/**
	 * Concatenates the values at <i>columnIndex</i> and <i>concatColumnIndex</i> on the given line,
	 * and stores the result at <i>columnIndex</i>.<br>
	 *
	 * Whether the value at <i>concatColumnIndex</i> goes to the left or to the right of the value at <i>columnIndex</i>
	 * depends on the type of the given {@link AbstractConcat}.
	 *
	 * @param line				The line on which the concatenation will be performed.
	 * @param columnIndex		The index of the column
	 *                          holding the value that should be concatenated to the value at <i>concatColumnIndex</i>.
	 *                          The result will be stored at <i>columnIndex</i>.
	 * @param concatColumnIndex	The index of the column
	 *                          holding the value that should be concatenated to the value at <i>columnIndex</i>.
	 * @param abstractConcat	The {@link AbstractConcat} used to determine
	 *                          whether the concatenation should be a left concatenation or a right concatenation.
	 */
	private void concatColumns(List<String> line, int columnIndex, int concatColumnIndex,
							   AbstractConcat abstractConcat) {

		String result = abstractConcat instanceof LeftConcat
			? line.get(concatColumnIndex).concat(line.get(columnIndex))
			: line.get(columnIndex).concat(line.get(concatColumnIndex));

		line.set(columnIndex, result);
	}

	/**
	 * Concatenates the value at <i>columnIndex</i> on the given line
	 * with the given {@link AbstractConcat}'s <i>getString()</i> value,
	 * and stores the result at <i>columnIndex</i>.<br>
	 *
	 * Whether {@link AbstractConcat#getString()} goes to the left or to the right of the value at <i>columnIndex</i>
	 * depends on the type of {@link AbstractConcat}.
	 *
	 * @param line				The line on which the concatenation will be performed.
	 * @param columnIndex		The index of the column
	 *                          holding the value that should be concatenated to {@link AbstractConcat#getString()}.
	 *                          The result will be stored at <i>columnIndex</i>.
	 * @param abstractConcat	The {@link AbstractConcat} used to determine
	 *                          whether the concatenation should be a left concatenation or a right concatenation.
	 */
	private void concatString(List<String> line, int columnIndex, AbstractConcat abstractConcat) {

		String result = abstractConcat instanceof LeftConcat
				? abstractConcat.getString().concat(line.get(columnIndex))
				: line.get(columnIndex).concat(abstractConcat.getString());

		line.set(columnIndex, result);
	}

	@Override
	public void visit(final Multiply multiply) {
		if (multiply == null) {
			log.warn("Compute Operation (Multiply) is null, the table remains unchanged.");
			return;
		}

		Integer columnIndex = multiply.getColumn();
		String operand2 = multiply.getMultiplyBy();
		
		if (columnIndex == null || operand2 == null ) {
			log.warn("Arguments in Compute Operation (Multiply) : {} are wrong, the table remains unchanged.", multiply);
			return;
		}

		if (columnIndex < 1 ) {
			log.warn("The index of the column to multiply cannot be < 1, the multiplication computation cannot be performed.");
			return;
		}

		performMathematicalOperation(multiply, columnIndex, operand2);
	}

	@Override
	public void visit(final PerBitTranslation perBitTranslation) {

		if (!perBitTranslationCheck(perBitTranslation)) {
			return;
		}

		Map<String, String> translations = perBitTranslation.getBitTranslationTable().getTranslations();
		int columnIndex = perBitTranslation.getColumn() - 1;
		List<Integer> bitList = perBitTranslation.getBitList();

		for (List<String> line : sourceTable.getTable()) {

			if (columnIndex < line.size()) {

				int valueToBeReplacedInt;

				try {
					valueToBeReplacedInt = Integer.parseInt(line.get(columnIndex));
				} catch (NumberFormatException e) {
					log.warn("Data is not correctly formatted.");
					return;
				}

				List<String> columnResult = translate(bitList, valueToBeReplacedInt, translations);

				if (!columnResult.isEmpty()) {
					String separator = HardwareConstants.WHITE_SPACE + HardwareConstants.DASH + HardwareConstants.WHITE_SPACE;

					line.set(columnIndex,
						columnResult
							.stream()
							.map(value -> String.join(separator, value))
							.collect(Collectors.joining(separator)));
				}
			}
		}
	}

	/**
	 * @param bitList			The list of bits that need to be checked.
	 * @param valueToReplace	The integer value that is being translated.
	 * @param translations		The reference dictionary used for translations.
	 *
	 * @return					A {@link List} of all the available translations for the given integer value.
	 */
	private List<String> translate(List<Integer> bitList, int valueToReplace, Map<String, String> translations) {

		List<String> result = new ArrayList<>();

		String translation;
		for (Integer bit : bitList) {

			translation = ((int) Math.pow(2, bit) & valueToReplace) != 0
						? translate(bit.toString(), translations, PER_BIT_MATCHES_TRANSLATION_FUNCTION)
						: translate(bit.toString(), translations, PER_BIT_NOT_MATCHES_TRANSLATION_FUNCTION);

			if (translation != null) {
				result.add(translation);
			}
		}

		return result;
	}

	/**
	 * PerBitTranslation visit check.
	 *
	 * @param perBitTranslation	The {@link PerBitTranslation} being checked.
	 *
	 * @return					<b>true</b> if the given {@link PerBitTranslation} is well-formed.<br>
	 * 							<b>false</b> otherwise.
	 */
	private boolean perBitTranslationCheck(final PerBitTranslation perBitTranslation) {

		if (perBitTranslation == null) {
			log.warn("The Source (PerBitTranslation) to visit is null, the PerBitTranslation computation cannot be performed.");
			return false;
		}

		TranslationTable bitTranslationTable = perBitTranslation.getBitTranslationTable();
		if (bitTranslationTable == null) {
			log.warn("TranslationTable is null, the PerBitTranslation computation cannot be performed.");
			return false;
		}

		Map<String, String> translations = bitTranslationTable.getTranslations();
		if (translations == null) {
			log.warn("The Translation Map {} is null, the PerBitTranslation computation cannot be performed.",
					bitTranslationTable.getName());
			return false;
		}

		int columnIndex = perBitTranslation.getColumn() - 1;
		if (columnIndex < 0) {
			log.warn("The index of the column to translate cannot be < 1, the PerBitTranslation computation cannot be performed.");
			return false;
		}

		List<Integer> bitList = perBitTranslation.getBitList();
		if (bitList == null) {
			log.warn("BitList is null, the PerBitTranslation computation cannot be performed.");
			return false;
		}

		return true;
	}

	/**
	 * Translates <i>valueToTranslate</i> using <i>translationMap</i> in <i>translationFunction</i>.
	 *
	 * @param valueToTranslate		The value being translated.
	 * @param translationMap		The reference dictionary used for the translation.
	 * @param translationFunction	The function used to perform the translation.
	 *
	 * @return						The translation of <i>valueToTranslate</i>.
	 */
	private String translate(final String valueToTranslate, final Map<String, String> translationMap, final BiFunction<String, Map<String, String>, String> translationFunction) {
		return translationFunction.apply(valueToTranslate, translationMap);
	}

	@Override
	public void visit(final Replace replace) {
		if (replace == null) {
			log.warn("Compute Operation (Replace) is null, the table remains unchanged.");
			return;
		}

		Integer columnToReplace = replace.getColumn();
		String strToReplace = replace.getReplace();
		String replacement = replace.getReplaceBy();

		if (columnToReplace == null || strToReplace == null || replacement == null) {
			log.warn("Arguments in Compute Operation (Replace) : {} are wrong, the table remains unchanged.", replace);
			return;
		}

		if (columnToReplace < 1) {
			log.warn("The index of the column to replace cannot be < 1, the replacement computation cannot be performed.");
			return;
		}

		int columnIndex = columnToReplace - 1;

		// If replacement is like "Column(n)", we replace the strToReplace by the content of the column n.
		if (COLUMN_PATTERN.matcher(replacement).matches()) {
			int replacementColumnIndex = Integer.parseInt(replacement.substring(
					replacement.indexOf(HardwareConstants.OPENING_PARENTHESIS) + 1, 
					replacement.indexOf(HardwareConstants.CLOSING_PARENTHESIS))) - 1;

			if (replacementColumnIndex < sourceTable.getTable().get(0).size()) {
				sourceTable.getTable()
				.forEach(column -> column.set(
						columnIndex, 
						column.get(columnIndex).replace(strToReplace, column.get(replacementColumnIndex)))
						);
			}
		} else {
			sourceTable.getTable()
			.forEach(column -> column.set(columnIndex, column.get(columnIndex).replace(strToReplace, replacement)));
		}

		sourceTable.setTable(SourceTable.csvToTable(SourceTable.tableToCsv(sourceTable.getTable(), HardwareConstants.SEMICOLON), HardwareConstants.SEMICOLON));
	}

	@Override
	public void visit(final RightConcat rightConcat) {

		processAbstractConcat(rightConcat);
	}

	@Override
	public void visit(final Substract substract) {

		if (substract == null) {
			log.warn("Compute Operation (Substract) is null, the table remains unchanged.");
			return;
		}

		Integer columnIndex = substract.getColumn();
		String operand2 = substract.getSubstract();

		if (columnIndex == null || operand2 == null ) {

			log.warn("Arguments in Compute Operation (Substract) : {} are wrong, the table remains unchanged.",
				substract);

			return;
		}

		if (columnIndex < 1 ) {
			log.warn("The index of the column to add cannot be < 1, the addition computation cannot be performed.");
			return;
		}

		performMathematicalOperation(substract, columnIndex, operand2);
	}

	@Override
	public void visit(final Substring substring) {
		if (!checkSubstring(substring)) {
			log.warn("The substring {} is not valid, the table remains unchanged.", substring);
			return;
		}

		final String start = substring.getStart();
		final String length = substring.getLength();

		final Integer startColumnIndex = getColumnIndex(start);
		if (!checkValueAndColumnIndexConsistency(start, startColumnIndex)) {
			log.warn("Inconsistent substring start value {}, the table remains unchanged.", start);
			return;
		}

		final Integer lengthColumnIndex = getColumnIndex(length);
		if (!checkValueAndColumnIndexConsistency(length, lengthColumnIndex)) {
			log.warn("Inconsistent substring length value {}, the table remains unchanged.", length);
			return;
		}

		performSubstring(substring.getColumn() - 1, start, startColumnIndex, length, lengthColumnIndex);
	}

	/**
	 * Check the given {@link Substring} instance
	 * 
	 * @param substring The substring instance we wish to check
	 * @return true if the substring is valid
	 */
	static boolean checkSubstring(final Substring substring) {
		return substring != null
				&& substring.getColumn() != null
				&& substring.getColumn() >= 1
				&& substring.getStart() != null
				&& substring.getLength() != null;
	}

	/**
	 * Perform a substring operation on the column identified by the given <code>columnIndex</code>
	 * 
	 * @param columnIndex      The column number in the current {@link SourceTable}
	 * @param start            The begin index, inclusive and starts at 1
	 * @param startColumnIndex The column index, so that we extract the start index. If equals -1 then it is not used
	 * @param end              The ending index, exclusive
	 * @param endColumnIndex   The column index, so that we extract the length index. If equals -1 then it is not used
	 */
	void performSubstring(final int columnIndex, final String start, final int startColumnIndex,
			final String end, final int endColumnIndex) {

		final TriFunction<List<String>, Integer, String, Integer> startFunction = getValueFunction(startColumnIndex);
		final TriFunction<List<String>, Integer, String, Integer> endFunction = getValueFunction(endColumnIndex);

		sourceTable.getTable()
		.forEach(row ->  {
			if (columnIndex < row.size()) {
				final String columnValue = row.get(columnIndex);
				final Integer beginIndex = startFunction.apply(row, startColumnIndex, start);
				final Integer endIndex = endFunction.apply(row, endColumnIndex, end);

				if (checkSubstringArguments(beginIndex, endIndex, columnValue.length())) {
					// No need to put endIndex -1 as the String substring end index is exclusive 
					// PSL substr(1,3) is equivalent to Java String substring(0, 3)
					row.set(columnIndex, columnValue.substring(beginIndex -1, endIndex));
					return;
				}
				log.warn("substring arguments are not valid: start={}, end={},"
						+ " startColumnIndex={}, endColumnIndex={},"
						+ " computed beginIndex={}, computed endInex={},"
						+ " row={}, columnValue={}",
						start, end,
						startColumnIndex, endColumnIndex,
						beginIndex, endIndex,
						row, columnValue);
			}

			log.warn("Cannot perform substring on row {} on column index {}", row, columnIndex);
		});
	}

	/**
	 * Check the substring argument to avoid the {@link StringIndexOutOfBoundsException}
	 * 
	 * @param begin  Starts from 1
	 * @param end    The end index of the string
	 * @param length The length of the {@link String}
	 * @return <code>true</code> if a {@link String} substring can be performed
	 */
	static boolean checkSubstringArguments(final Integer begin, final Integer end, final int length) {
		return begin != null
				&& end != null
				&& (begin - 1) >= 0
				&& (begin - 1) <= end
				&& end <= length;
	}

	/**
	 * Transform the given {@link String} value to an {@link Integer} value
	 * 
	 * @param value The value we wish to parse
	 * @return {@link Integer} value
	 */
	static Integer transformToIntegerValue(final String value) {
		if (value != null && value.matches("\\d+")) {
			return Integer.parseInt(value);
		}
		return null;
	}

	/**
	 * Return the right {@link TriFunction} based on the <code>foreignColumnIndex</code>
	 * 
	 * @param foreignColumnIndex The index of the column we wish to check so that we choose the right function to return
	 * @return {@link TriFunction} used to get the value
	 */
	static TriFunction<List<String>, Integer, String, Integer> getValueFunction(final int foreignColumnIndex) {
		if (foreignColumnIndex  >= 0) {
			return  GET_VALUE_FROM_ROW;
		} else {
			return  GET_VALUE;
		}
	}

	/**
	 * Check value and column index consistency. At least we need one data available
	 * 
	 * @param value              The string value as a number
	 * @param foreignColumnIndex The index of the column already extracted from a value expected as <em>Column($index)</em>
	 * @return <code>true</code> if data is consistent
	 */
	static boolean checkValueAndColumnIndexConsistency(final String value, final Integer foreignColumnIndex) {
		return foreignColumnIndex >= 0 || value.matches("\\d+");
	}

	/**
	 * Get the column index for the given value
	 * 
	 * @param value The value we wish to parse
	 * @return {@link Integer} value or -1 if value is not in the column pattern format
	 */
	static Integer getColumnIndex(final String value) {
		final Matcher matcher = COLUMN_PATTERN.matcher(value);
		return matcher.matches() ? Integer.parseInt(matcher.group(1)) - 1 : -1;
	}

	@Override
	public void visit(final Translate translate) {

		if (translate == null) {
			log.warn("The Source (Translate) to visit is null, the translate computation cannot be performed.");
			return;
		}

		TranslationTable translationTable = translate.getTranslationTable();
		if (translationTable == null) {
			log.warn("TranslationTable is null, the translate computation cannot be performed.");
			return;
		}

		Map<String, String> translations = translationTable.getTranslations();
		if (translations == null) {
			log.warn("The Translation Map {} is null, the translate computation cannot be performed.",
					translationTable.getName());
			return;
		}

		int columnIndex = translate.getColumn() - 1;
		if (columnIndex < 0) {
			log.warn("The index of the column to translate cannot be < 1, the translate computation cannot be performed.");
			return;
		}

		for (List<String> line : sourceTable.getTable()) {

			if (columnIndex < line.size()) {
				String valueToBeReplaced = line.get(columnIndex);
				String newValue = translate(valueToBeReplaced, translations, TRANSLATION_FUNCTION);

				if (newValue != null) {
					line.set(columnIndex, newValue);
				} else {
					log.warn("The Translation Map {} does not contain the following value {}.",
							translationTable.getName(), valueToBeReplaced);
				}
			}
		}
	}

	@Override
	public void visit(final XML2CSV xml2csv) {
		// Not implemented yet
	}


	/**
	 * Perform a mathematical computation (add, subtract, multiply or divide) on a given column in the sourceTable
	 * Check if the operand2 is a reference to a column or a raw value 
	 * @param computeOperation The compute operation must be one of : Add, Substract, Multiply, Divide.
	 * @param column column to be changed
	 * @param operand2 can be a reference to another column or a raw value
	 */
	private void performMathematicalOperation(final Compute computeOperation, Integer column, String operand2) {

		if (!MATH_FUNCTIONS_MAP.containsKey(computeOperation.getClass())) {
			log.warn("The compute operation must be one of : Add, Substract, Multiply, Divide.");
			return;
		}

		Integer columnIndex = column - 1;
		int operandByIndex = -1;

		Matcher matcher = COLUMN_PATTERN.matcher(operand2);
		if (matcher.matches()) {
			try {
				operandByIndex = Integer.parseInt(matcher.group(1)) - 1;
				if (operandByIndex < 0) {
					log.warn("The operand2 column index cannot be < 1, the {} computation cannot be performed, the table remains unchanged.", computeOperation.getClass());
					return;
				}
			} catch (NumberFormatException e) {
				log.warn("NumberFormatException : {} is not a correct operand2 for {}, the table remains unchanged.", operand2, computeOperation);
				log.warn("Exception : ", e);
				return;
			}

		} else if (!operand2.matches("\\d+")) {
			log.warn("operand2 is not a number: {}, the table remains unchanged.", operand2);
			return;
		}

		
		performMathComputeOnTable(computeOperation, columnIndex, operand2, operandByIndex);
	}

	/**
	 * Execute the computational operation (Add, Substract, Divide or Multiply) on each row of the tableSource.
	 *
	 * @param computeOperation	The {@link Compute} operation that should be performed.
	 * @param columnIndex		The index of the column on which the operation should be performed.
	 * @param op2               The second operand of the operation.
	 * @param op2Index			The column holding the value of the second operand in the {@link SourceTable}.
	 */
	private void performMathComputeOnTable(final Compute computeOperation, Integer columnIndex, String op2, int op2Index) {
		for (List<String> line : sourceTable.getTable()) {

			if (columnIndex < line.size()) {
				String op1 = line.get(columnIndex);

				if (op2Index != -1) {
					if (op2Index < line.size()) {
						performMathComputeOnLine(computeOperation.getClass(), columnIndex, line, op1, line.get(op2Index));
					}
				} else {

					performMathComputeOnLine(computeOperation.getClass(), columnIndex, line, op1, op2);

				}
			}
		}
	}

	/**
	 * Given two operands, perform an addition, substraction, multiplication or division
	 * and modify the given line on the given columnIndex.
	 *
	 * @param computeOperation	The {@link Compute} operation that should be performed.
	 * @param columnIndex		The index of the column on which the operation should be performed.
	 * @param line				The row of the {@link SourceTable} that is being operated on.
	 * @param op1				The first operand of the operation.
	 * @param op2				The second operand of the operation.
	 */
	private void performMathComputeOnLine(final Class<? extends Compute> computeOperation, final Integer columnIndex,
			final List<String> line, final String op1, final String op2) {
		try {
			if(MATH_FUNCTIONS_MAP.containsKey(computeOperation)) {
				String resultFunction = MATH_FUNCTIONS_MAP.get(computeOperation).apply(op1, op2);
				if (resultFunction != null) {
					line.set(columnIndex, resultFunction);
				}
			}
		} catch (NumberFormatException e) {
			log.warn("There is a NumberFormatException on operand 1 : {} or the operand 2 {}", op1, op2);
			log.warn("Exception : ", e);
		} 
	}
}
