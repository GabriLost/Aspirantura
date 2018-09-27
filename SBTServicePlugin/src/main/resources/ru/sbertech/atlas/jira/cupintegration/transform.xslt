<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dyn="http://exslt.org/dynamic"
                extension-element-prefixes="dyn" version="1.0">
    <xsl:param name="ID_KRP_CUSTOM_FIELD"/>
    <xsl:template match="/">
        <timesheets sync_time="{rss/channel/build_date}">
            <xsl:for-each select="rss/channel/item">
                <timesheet>
                    <task>
                        <xsl:attribute name="ps_task_id">
                            <xsl:value-of select="key/@id"/>
                        </xsl:attribute>
                        <xsl:attribute name="pr_task_id">
                            <xsl:value-of select="dyn:evaluate(concat($ID_KRP_CUSTOM_FIELD, '/customfieldvalues/customfieldvalue'))"/>
                        </xsl:attribute>
                        <xsl:value-of select="title"/>

                    </task>
                    <ps>
                        <xsl:attribute name="ps_id">1</xsl:attribute>
                        JIRA
                    </ps>
                    <row_type>
                        <xsl:attribute name="type_id">1</xsl:attribute>
                        Трудозатраты
                    </row_type>
                    <rfc>
                        <xsl:attribute name="id_ascup">-1</xsl:attribute>
                        <xsl:attribute name="id_ps">
                            <xsl:value-of select="key"/>
                        </xsl:attribute>
                        <xsl:attribute name="ps_href">
                            <xsl:value-of select="link"/>
                        </xsl:attribute>
                    </rfc>
                    <actual_future_effort_all>
                        <xsl:value-of select="timeoriginalestimate/@hours"/>
                    </actual_future_effort_all>
                    <text_row>

                    </text_row>
                    <resources>
                        <xsl:copy-of select="resources/*"/>
                    </resources>
                </timesheet>
            </xsl:for-each>
        </timesheets>
    </xsl:template>
</xsl:stylesheet>
