package com.wfbfm.portyj.position;

import com.wfbfm.portyj.model.CanaccordPosition;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CanaccordPositionLoader
{

    public CanaccordPositionLoader()
    {
    }

    public List<CanaccordPosition> parse(final String csvFileName) throws IOException
    {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(csvFileName))
        {
            return parse(is);
        }
    }

    public List<CanaccordPosition> parse(final InputStream csvStream) throws IOException
    {
        try (
                Reader reader = new InputStreamReader(csvStream, StandardCharsets.UTF_8);
                CSVParser parser = CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withTrim()
                        .parse(reader)
        )
        {
            List<CanaccordPosition> positions = new ArrayList<>();

            for (CSVRecord record : parser)
            {
                CanaccordPosition p = new CanaccordPosition();
                p.setAssetName(record.get("Asset name"));
                p.setAccount(record.get("Account"));
                p.setCurrency(record.get("CCY"));
                p.setPriceCurrency(record.get("Price CCY"));
                p.setIsin(record.get("ISIN/ID"));

                p.setPercentOfTotal(parsePercent(record.get("% of total")));
                p.setBookCost(parseDecimal(record.get("Book cost")));
                p.setValue(parseDecimal(record.get("Value")));
                p.setUnrealisedGain(parseDecimal(record.get("Unrealised gain")));
                p.setPrice(parseDecimal(record.get("Price")));
                p.setQuantity(parseDecimal(record.get("Quantity")));
                p.setFxRate(parseDecimal(record.get("F/X")));

                p.setAsOf(parseDate(record.get("As of")));

                positions.add(p);
            }

            return positions;
        }
    }

    private static BigDecimal parseDecimal(String raw)
    {
        if (raw == null || raw.isBlank()) return null;

        String cleaned = raw
                .replace(",", "")
                .replace("\"", "")
                .trim();

        boolean negative = cleaned.startsWith("(") && cleaned.endsWith(")");
        cleaned = cleaned.replace("(", "").replace(")", "");

        BigDecimal value = new BigDecimal(cleaned);
        return negative ? value.negate() : value;
    }

    private static BigDecimal parsePercent(String raw)
    {
        if (raw == null || raw.isBlank()) return null;
        return new BigDecimal(raw.replace("%", "")).movePointLeft(2);
    }

    private static LocalDate parseDate(String raw)
    {
        if (raw == null || raw.isBlank()) return null;
        return LocalDate.parse(raw, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
