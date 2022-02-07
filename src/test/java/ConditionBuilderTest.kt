import de.byjoker.jfql.statement.ConditionBuilder
import org.junit.Test
import kotlin.test.assertEquals

class ConditionBuilderTest {

    @Test
    fun `standalone condition building test`() {
        assertEquals(
            "test === 'a'",
            ConditionBuilder("test").`is`().equals("a").build().condition()
        )

        assertEquals(
            "test !== 'a'",
            ConditionBuilder("test").not().equals("a").build().condition()
        )

        assertEquals(
            "test != equals_ignore_case:'a'",
            ConditionBuilder("test").not().equalsIgnoreCase("a").build().condition()
        )
    }

}
