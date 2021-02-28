/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jun0rr.doxy.common;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.jun0rr.doxy.cfg.Host;
import us.pserver.tools.Unchecked;


/**
 *
 * @author Juno
 */
public class StringValue {
  
  private final String value;
  
  public StringValue(String val) {
    this.value = Conditional.<String>of(v->v != null && !v.isBlank())
        .elseThrow(v->new IllegalArgumentException("Bad string value: " + v))
        .apply(val).get();
  }
  
  public static StringValue of(String val) {
    return new StringValue(val);
  }
  
  
  public Double getAsDouble() {
    return DOUBLE_PARSER.parse(value);
  }
  
  public Integer getAsInt() {
    return INT_PARSER.parse(value);
  }
  
  public Long getAsLong() {
    return LONG_PARSER.parse(value);
  }
  
  public Host getAsHost() {
    return HOST_PARSER.parse(value);
  }
  
  public Range getAsRange() {
    return RANGE_PARSER.parse(value);
  }
  
  public InetAddress getAsInetAddress() {
    return IP_PARSER.parse(value);
  }
  
  public LocalDate getAsLocalDate() {
    return LOCAL_DATE_PARSER.parse(value);
  }
  
  public LocalDateTime getAsLocalDateTime() {
    return LOCAL_DATE_TIME_PARSER.parse(value);
  }
  
  public LocalTime getAsLocalTime() {
    return LOCAL_TIME_PARSER.parse(value);
  }
  
  public Instant getAsInstant() {
    return INSTANT_PARSER.parse(value);
  }
  
  public Boolean getAsBoolean() {
    return BOOLEAN_PARSER.parse(value);
  }
  
  public List<StringValue> getAsList() {
    return LIST_PARSER.parse(value);
  }
  
  public Path getAsPath() {
    return PATH_PARSER.parse(value);
  }
  
  public char[] getAsCharArray() {
    return value.toCharArray();
  }
  
  public Object getAsObject() {
    return PARSER_LIST.stream()
        .filter(p->p.canParse(value))
        .map(p->p.parse(value))
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException(String.format("Unknown format! Cannot parse value <%s>", value)));
  }
  
  public <T> T getAs(Class<T> cls) {
    return PARSER_LIST.stream()
        .filter(p->p.isTypeOf(cls))
        .map(p->(ValueParser<T>)p)
        .findFirst()
        .orElseThrow(()->new IllegalArgumentException(String.format("Unknown type <%s>! Cannot parse value <%s>", cls, value)))
        .parse(value);
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.value);
    return hash;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final StringValue other = (StringValue) obj;
    return Objects.equals(this.value, other.value);
  }
  
  @Override
  public String toString() {
    return value;
  }
  


  public static final Predicate<String> BOOLEAN_PATTERN = Pattern.compile("(true|false)", Pattern.CASE_INSENSITIVE).asMatchPredicate();
  
  public static final Predicate<String> DOUBLE_PATTERN = Pattern.compile("(-|\\+)?[0-9]+\\.[0-9]+").asMatchPredicate();
  
  public static final Predicate<String> INT_PATTERN = Pattern.compile("\\-?[0-9]{1,9}").asMatchPredicate();
  
  public static final Predicate<String> LONG_PATTERN = Pattern.compile("\\-?[0-9]{10,19}").asMatchPredicate();
  
  public static final Predicate<String> LOCAL_DATE_DDMMYYYY_PATTERN = Pattern.compile("[0-9]{2}\\/[0-9]{2}\\/[0-9]{4}").asMatchPredicate();
  
  public static final Predicate<String> LOCAL_DATE_YYYYMMDD_PATTERN = Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}").asMatchPredicate();
  
  public static final Predicate<String> LOCAL_DATE_TIME_PATTERN = Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?").asMatchPredicate();
  
  public static final Predicate<String> LOCAL_DATE_TIME_ISO_PATTERN = Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}(\\.[0-9]+)?").asMatchPredicate();
  
  public static final Predicate<String> INSTANT_PATTERN = Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]+Z").asMatchPredicate();
  
  public static final Predicate<String> LOCAL_TIME_PATTERN = Pattern.compile("[0-9]{2}:[0-9]{2}(:[0-9]{2})?(\\.[0-9]+)?").asMatchPredicate();
  
  public static final Predicate<String> LIST_PATTERN = Pattern.compile("([\\w\\.\\-\\/:]+,)+[\\w\\.\\-\\/:]+").asMatchPredicate();
  
  public static final Predicate<String> IP_PATTERN = Pattern.compile("(([0-9a-fA-F]{1,4}[\\.:]{0,2}){1,7}[0-9a-fA-F]{1,4})$").asMatchPredicate();
  
  public static final Predicate<String> HOST_PATTERN = Pattern.compile("(([0-9a-fA-F]{1,4}[\\.:]{0,2}){1,7}[0-9a-fA-F]{1,4})(:[0-9]{1,5})").asMatchPredicate();
  
  public static final Predicate<String> PATH_PATTERN = Pattern.compile("((\\/[a-zA-Z0-9\\._@]+)|([a-zA-Z0-9\\._@]+\\/))+([a-zA-Z0-9_\\.@\\/]+)?").asMatchPredicate();
  
  public static final Predicate<String> RANGE_PATTERN = Pattern.compile("\\{[0-9]+,[0-9]+\\}").asMatchPredicate();
  
  
  public static interface ValueParser<T> {

    public T parse(String s);

    public boolean canParse(String str);

    public boolean isTypeOf(Class c);

    public static <U> ValueParser<U> of(Predicate<String> canParse, Predicate<Class> typeOf, Function<String,U> parse) {
      return new ValueParser<U>() {
        @Override
        public U parse(String s) {
          return parse.apply(s);
        }
        @Override
        public boolean canParse(String str) {
          return canParse.test(str);
        }
        @Override
        public boolean isTypeOf(Class c) {
          return typeOf.test(c);
        }
      };
    }

  }
  

  public static final ValueParser<Boolean> BOOLEAN_PARSER = ValueParser.of(
      BOOLEAN_PATTERN, 
      c->Boolean.class.isAssignableFrom(c) || boolean.class.isAssignableFrom(c), 
      Boolean::parseBoolean
  );
  
  public static final ValueParser<Double> DOUBLE_PARSER = ValueParser.of(
      DOUBLE_PATTERN, 
      c->Double.class.isAssignableFrom(c) || double.class.isAssignableFrom(c), 
      Double::parseDouble
  );
  
  public static final ValueParser<Integer> INT_PARSER = ValueParser.of(
      INT_PATTERN, 
      c->Integer.class.isAssignableFrom(c) || int.class.isAssignableFrom(c), 
      Integer::parseInt
  );
  
  public static final ValueParser<Long> LONG_PARSER = ValueParser.of(
      LONG_PATTERN, 
      c->Long.class.isAssignableFrom(c) || long.class.isAssignableFrom(c), 
      Long::parseLong
  );
  
  public static final ValueParser<LocalDate> LOCAL_DATE_PARSER = ValueParser.of(
      s->LOCAL_DATE_DDMMYYYY_PATTERN.test(s) || LOCAL_DATE_YYYYMMDD_PATTERN.test(s),
      LocalDate.class::isAssignableFrom, 
      s->Conditional.<String,LocalDate>of(LOCAL_DATE_DDMMYYYY_PATTERN, v->LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy").parse(v)))
        .elseIf(LOCAL_DATE_YYYYMMDD_PATTERN, v->LocalDate.from(DateTimeFormatter.ISO_LOCAL_DATE.parse(v)))
        .elseThrow(v->new IllegalArgumentException("Not a LocalDate value: " + v))
        .apply(s).orElse(null)
  );
  
  public static final ValueParser<LocalDateTime> LOCAL_DATE_TIME_PARSER = ValueParser.of(
      s->LOCAL_DATE_TIME_PATTERN.test(s) || LOCAL_DATE_TIME_ISO_PATTERN.test(s),
      LocalDateTime.class::isAssignableFrom, 
      s->Conditional.<String,LocalDateTime>of(LOCAL_DATE_TIME_PATTERN, v->LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(v)))
        .elseIf(LOCAL_DATE_TIME_ISO_PATTERN, v->LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(v)))
        .elseThrow(v->new IllegalArgumentException("Not a LocalDateTime value: " + v))
        .apply(s).orElse(null)
  );
  
  public static final ValueParser<LocalTime> LOCAL_TIME_PARSER = ValueParser.of(
      LOCAL_TIME_PATTERN::test,
      LocalTime.class::isAssignableFrom, 
      LocalTime::parse
  );
  
  public static final ValueParser<Instant> INSTANT_PARSER = ValueParser.of(
      INSTANT_PATTERN::test,
      Instant.class::isAssignableFrom, 
      Instant::parse
  );
  
  public static final ValueParser<List<StringValue>> LIST_PARSER = ValueParser.of(LIST_PATTERN::test,
      List.class::isAssignableFrom, 
      s->List.of(s.split(",")).stream()
        .map(StringValue::of)
        .collect(Collectors.toList())
  );
  
  public static final ValueParser<InetAddress> IP_PARSER = ValueParser.of(
      IP_PATTERN::test,
      InetAddress.class::isAssignableFrom, 
      v->Unchecked.call(()->InetAddress.getByName(v))
  );
  
  public static final ValueParser<Host> HOST_PARSER = ValueParser.of(
      HOST_PATTERN::test,
      Host.class::isAssignableFrom, 
      Host::of
  );
  
  public static final ValueParser<Path> PATH_PARSER = ValueParser.of(
      PATH_PATTERN::test,
      Path.class::isAssignableFrom, 
      Paths::get
  );
  
  public static final ValueParser<Range> RANGE_PARSER = ValueParser.of(
      RANGE_PATTERN::test,
      Range.class::isAssignableFrom, 
      Range::of
  );
  
  
  public static final List<ValueParser<?>> PARSER_LIST = new LinkedList<>(List.of(
      BOOLEAN_PARSER,
      DOUBLE_PARSER,
      INT_PARSER,
      LONG_PARSER,
      LOCAL_DATE_PARSER,
      LOCAL_DATE_TIME_PARSER,
      LOCAL_TIME_PARSER,
      INSTANT_PARSER,
      LIST_PARSER,
      IP_PARSER,
      HOST_PARSER,
      PATH_PARSER,
      RANGE_PARSER,
      ValueParser.of(s->true, Object.class::isAssignableFrom, Objects::toString)
  ));
  
  
  public static void insertParser(ValueParser vp) {
    PARSER_LIST.add(0, vp);
  }
  
  public static void appendParser(ValueParser vp) {
    PARSER_LIST.add(PARSER_LIST.size() -1, vp);
  }
  
  public static Optional<ValueParser<?>> getParserFor(String val) {
    return PARSER_LIST.stream().filter(p->p.canParse(val)).findFirst();
  }
  
  public static Optional<ValueParser<?>> getParserFor(Class c) {
    return PARSER_LIST.stream().filter(p->p.isTypeOf(c)).findFirst();
  }
  
}
