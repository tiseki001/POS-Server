<?xml version="1.0" encoding="UTF-8"?>
<Form xmlVersion="20140501" releaseVersion="7.1.1">
<TableDataMap>
<TableData name="ds1" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="year"/>
<O>
<![CDATA[2012]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[select a.年度,工资合计,销售收入,round(工资合计/销售收入,2) as 工资销售比,利润,round(工资合计/利润,2) as 工资利润比 from (SELECT 年度,
sum(合计) as 工资合计
FROM 岗位工资统计
where 1=1 ${if(len(year)==0,"","and 年度="+year+"")}
group by 年度) as a
inner join 
(SELECT 年度,销售收入,利润 FROM 收入利润
where 1=1 ${if(len(year)==0,"","and 年度="+year+"")}) as b
on a.年度=b.年度]]></Query>
</TableData>
<TableData name="ds2" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="year"/>
<O>
<![CDATA[2012]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[select a.年度,round(金额/总金额,2) as 人力成本占比 from (SELECT 年度,sum(金额) as 总金额 FROM 各类成本记录
where 1=1 ${if(len(year)==0,"","and 年度="+year+"")}
group by 年度) as a
inner join
(SELECT 年度,金额 FROM 各类成本记录
where 项目='人力成本' ${if(len(year)==0,"","and 年度="+year+"")}) as b
on a.年度=b.年度]]></Query>
</TableData>
<TableData name="ds3" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="year"/>
<O>
<![CDATA[2012]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[select a.年度,round(工资合计/人数,2) as 人均工资 from (SELECT 年度,sum(合计) as 工资合计 FROM 岗位工资统计
where 1=1 ${if(len(year)==0,"","and 年度="+year+"")} group by 年度) as a
inner join
(SELECT ${year} as 年度,count(ID) as 人数 FROM 人员花名册) as b
on a.年度=b.年度]]></Query>
</TableData>
<TableData name="ds4" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="type"/>
<O>
<![CDATA[总工资]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[select 地区,${type} as 类型 from
(SELECT 地区,round(工资总额,2) as 总工资 ,round(工资总额/ count(S人员花名册.ID),2) as 人均工资
FROM 公司工资总额统计,S人员花名册
where S人员花名册.公司简称=公司工资总额统计.公司名称
group by 公司编码,公司名称,工资总额,地区)]]></Query>
</TableData>
<TableData name="ds5" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="p"/>
<O>
<![CDATA[浙江]]></O>
</Parameter>
<Parameter>
<Attributes name="year1"/>
<O>
<![CDATA[2010]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[SELECT * FROM 职能工资统计
where  年度=${year1} ${if(len(p)==0,"","and 公司简称='"+p+"分公司'")}
]]></Query>
</TableData>
<TableData name="ds6" class="com.fr.data.impl.DBTableData">
<Parameters>
<Parameter>
<Attributes name="p"/>
<O>
<![CDATA[浙江]]></O>
</Parameter>
<Parameter>
<Attributes name="year1"/>
<O>
<![CDATA[2010]]></O>
</Parameter>
</Parameters>
<Attributes maxMemRowCount="-1"/>
<Connection class="com.fr.data.impl.NameDatabaseConnection">
<DatabaseName>
<![CDATA[FRDemo]]></DatabaseName>
</Connection>
<Query>
<![CDATA[SELECT * FROM 岗位工资统计
where  年度=${year1} ${if(len(p)==0,"","and 公司简称='"+p+"分公司'")}

]]></Query>
</TableData>
</TableDataMap>
<Layout class="com.fr.form.ui.container.WBorderLayout">
<WidgetName name="form"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Center class="com.fr.form.ui.container.WFitLayout">
<WidgetName name="body"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="5" left="10" bottom="1" right="10"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="宋体" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Background name="ColorBackground" color="-328966"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-328966"/>
<LCAttr vgap="0" hgap="0" compInterval="5"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="label0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O>
<![CDATA[请选择年份：]]></O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="4" autoline="true"/>
<FRFont name="SimSun" style="0" size="72"/>
<border style="0" color="-723724"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="758" height="31"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WScaleLayout">
<WidgetName name="comboBox0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ComboBox">
<WidgetName name="year"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Dictionary class="com.fr.data.impl.CustomDictionary">
<CustomDictAttr>
<Dict key="2010" value="2010"/>
<Dict key="2011" value="2011"/>
<Dict key="2012" value="2012"/>
</CustomDictAttr>
</Dictionary>
<widgetValue>
<O>
<![CDATA[2010]]></O>
</widgetValue>
</InnerWidget>
<BoundsAttr x="758" y="0" width="200" height="21"/>
</Widget>
</InnerWidget>
<BoundsAttr x="758" y="0" width="200" height="31"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="chart0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="1" color="-2434342" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=\"人力成本占比\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="180" maxArrowAngle="180" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
<ChartDefinition>
<MeterTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds2]]></Name>
</TableData>
<MeterTable201109 meterType="1" name="年度" value="人力成本占比"/>
</MeterTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</InnerWidget>
<BoundsAttr x="717" y="38" width="241" height="122"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="人力成本占比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="241" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="人力成本占比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</title>
<body class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="180" maxArrowAngle="180" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
</Chart>
</Chart>
</body>
</InnerWidget>
<BoundsAttr x="717" y="31" width="241" height="160"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="chart1"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart1"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="1" color="-2434342" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=\"工资利润比\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="270" maxArrowAngle="270" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="微软雅黑" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="88" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="微软雅黑" style="0" size="80"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
<ChartDefinition>
<MeterTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds1]]></Name>
</TableData>
<MeterTable201109 meterType="1" name="年度" value="工资利润比"/>
</MeterTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</InnerWidget>
<BoundsAttr x="240" y="38" width="240" height="122"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="工资利润比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="240" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="工资利润比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</title>
<body class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart1"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="180" maxArrowAngle="180" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
</Chart>
</Chart>
</body>
</InnerWidget>
<BoundsAttr x="240" y="31" width="240" height="160"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="chart2"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart2"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="1" color="-2434342" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=\"工资销售比\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="270" maxArrowAngle="270" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
<ChartDefinition>
<MeterTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds1]]></Name>
</TableData>
<MeterTable201109 meterType="1" name="年度" value="工资销售比"/>
</MeterTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</InnerWidget>
<BoundsAttr x="0" y="38" width="240" height="122"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="工资销售比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="240" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="工资销售比"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</title>
<body class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart2"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="180" maxArrowAngle="180" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
<ChartDefinition>
<MeterTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds1]]></Name>
</TableData>
<MeterTable201109 meterType="1" name="年度" value="工资销售比"/>
</MeterTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</body>
</InnerWidget>
<BoundsAttr x="0" y="31" width="240" height="160"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="chart3"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart3"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="1" color="-2434342" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=\"人均工资\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="270" maxArrowAngle="270" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
<ChartDefinition>
<MeterTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds3]]></Name>
</TableData>
<MeterTable201109 meterType="1" name="年度" value="人均工资"/>
</MeterTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</InnerWidget>
<BoundsAttr x="480" y="38" width="237" height="122"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="人均工资"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="237" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="人均工资"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</title>
<body class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart3"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MeterPlot">
<MeterPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<MeterStyle>
<Attr meterAngle="180" maxArrowAngle="180" units="元" order="0" designType="0" tickLabelsVisible="true" dialShape="180" isShowTitle="true" meterType="0" startValue="=0.0" endValue="=180.0" tickSize="=20.0"/>
<titleTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="80"/>
</Attr>
</TextAttr>
</titleTextAttr>
<valueTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="1" size="144" foreground="-11683767"/>
</Attr>
</TextAttr>
</valueTextAttr>
<unitTextAttr>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft Yahei" style="0" size="96" foreground="-10461088"/>
</Attr>
</TextAttr>
</unitTextAttr>
<IntervalList>
<MeterInterval label="分段区域" backgroudColor="-1620162" beginValue="=0.0" endValue="=60.0"/>
<MeterInterval label="分段区域" backgroudColor="-208375" beginValue="=60.0" endValue="=120.0"/>
<MeterInterval label="分段区域" backgroudColor="-11683767" beginValue="=120.0" endValue="=180.0"/>
</IntervalList>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="100.0" useType="0" areaNumber="5" mainColor="-16776961"/>
</MapHotAreaColor>
</MeterStyle>
</MeterPlot>
</Plot>
</Chart>
</Chart>
</body>
</InnerWidget>
<BoundsAttr x="480" y="31" width="237" height="160"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.RadioGroup">
<WidgetName name="type"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Dictionary class="com.fr.data.impl.CustomDictionary">
<CustomDictAttr>
<Dict key="总工资" value="总工资"/>
<Dict key="人均工资" value="人均工资"/>
</CustomDictAttr>
</Dictionary>
<widgetValue>
<O>
<![CDATA[总工资]]></O>
</widgetValue>
</InnerWidget>
<BoundsAttr x="293" y="191" width="187" height="24"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.RadioGroup">
<WidgetName name="type1"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Dictionary class="com.fr.data.impl.CustomDictionary">
<CustomDictAttr>
<Dict key="0" value="0"/>
<Dict key="1" value="1"/>
</CustomDictAttr>
</Dictionary>
<widgetValue>
<O>
<![CDATA[0]]></O>
</widgetValue>
</InnerWidget>
<BoundsAttr x="780" y="191" width="178" height="24"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="chart4"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart4"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="1" color="-2434342" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=\"分公司工资发放\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MapPlot">
<MapPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<Legend>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr position="4" visible="true"/>
<FRFont name="SimSun" style="0" size="72"/>
</Legend>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<newattr201212 mapName="中国简称地图"/>
<MapHotAreaColor>
<MC_Attr minValue="0.0" maxValue="0.0" useType="0" areaNumber="5" mainColor="-16776961"/>
<ColorList>
<AreaColor>
<AC_Attr minValue="0.0" maxValue="0.0" color="-16776961"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="0.0" maxValue="0.0" color="-872414977"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="0.0" maxValue="0.0" color="-1728052993"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="0.0" maxValue="0.0" color="1694499071"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="0.0" maxValue="0.0" color="838861055"/>
</AreaColor>
</ColorList>
</MapHotAreaColor>
</MapPlot>
</Plot>
<ChartDefinition>
<MapSingleLayerTableDefinition>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
<TableData class="com.fr.data.impl.NameTableData">
<Name>
<![CDATA[ds4]]></Name>
</TableData>
<AreaName areaName="地区"/>
<DefinitionList>
<SeriesDefinition>
<SeriesName>
<O>
<![CDATA[金额]]></O>
</SeriesName>
<SeriesValue>
<O>
<![CDATA[类型]]></O>
</SeriesValue>
</SeriesDefinition>
</DefinitionList>
</MapSingleLayerTableDefinition>
</ChartDefinition>
</Chart>
</Chart>
</InnerWidget>
<BoundsAttr x="0" y="38" width="480" height="283"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="分公司工资发放"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="480" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[="分公司工资发放"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2434342"/>
</title>
<body class="com.fr.form.ui.ChartEditor">
<WidgetName name="chart4"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.MapPlot">
<MapPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="0" seriesDragEnable="true" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<Legend>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr position="4" visible="true"/>
<FRFont name="Microsoft YaHei" style="0" size="72"/>
</Legend>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<newattr201212 mapName="中国"/>
<MapHotAreaColor>
<MC_Attr minValue="200.0" maxValue="300.0" useType="0" areaNumber="5" mainColor="-16776961"/>
<ColorList>
<AreaColor>
<AC_Attr minValue="280.0" maxValue="300.0" color="-16776961"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="260.0" maxValue="280.0" color="-872414977"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="240.0" maxValue="260.0" color="-1728052993"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="220.0" maxValue="240.0" color="1694499071"/>
</AreaColor>
<AreaColor>
<AC_Attr minValue="200.0" maxValue="220.0" color="838861055"/>
</AreaColor>
</ColorList>
</MapHotAreaColor>
</MapPlot>
</Plot>
</Chart>
</Chart>
</body>
</InnerWidget>
<BoundsAttr x="0" y="215" width="480" height="321"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.container.WTitleLayout">
<WidgetName name="report0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="0" left="0" bottom="0" right="0"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<LCAttr vgap="0" hgap="0" compInterval="0"/>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.ElementCaseEditor">
<WidgetName name="report0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="1" left="1" bottom="1" right="1"/>
<Border>
<border style="1" color="-2368549" type="1" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[=$p+\"地区工资发放\"]]></O>
<FRFont name="微软雅黑" style="0" size="80"/>
<Position pos="2"/>
<Background name="ColorBackground" color="-1"/>
</WidgetTitle>
<Background name="ColorBackground" color="-1"/>
<Alpha alpha="1.0"/>
</Border>
<Background name="ColorBackground" color="-1"/>
<FormElementCase>
<ReportPageAttr>
<HR/>
<FR/>
<HC/>
<FC/>
</ReportPageAttr>
<ColumnPrivilegeControl/>
<RowPrivilegeControl/>
<RowHeight defaultValue="723900">
<![CDATA[723900,723900,723900,723900,723900,723900,723900,723900,723900,342900,723900,723900,723900,723900,723900,723900,723900,114300,723900]]></RowHeight>
<ColumnWidth defaultValue="2743200">
<![CDATA[2743200,2743200,2743200,2743200,2743200,2743200,0,0,0,0,0,0,0,0,0,0,2743200]]></ColumnWidth>
<CellElementList>
<C c="0" r="0" cs="6" rs="11">
<O t="CC">
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.Bar2DPlot">
<CategoryPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="-1" seriesDragEnable="false" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${SERIES}${BR}${CATEGORY}${BR}${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<Legend>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr position="4" visible="false"/>
<FRFont name="微软雅黑" style="0" size="72"/>
</Legend>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<RectanglePlotAttr interactiveAxisTooltip="false"/>
<xAxis>
<CategoryAxis class="com.fr.chart.chartattr.CategoryAxis">
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="0"/>
<newLineColor mainGridColor="-4144960" lineColor="-5197648"/>
<AxisPosition value="3"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</CategoryAxis>
</xAxis>
<yAxis>
<ValueAxis class="com.fr.chart.chartattr.ValueAxis">
<ValueAxisAttr201108 alignZeroValue="false"/>
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="1"/>
<newLineColor mainGridColor="-263173" lineColor="-5197648"/>
<AxisPosition value="2"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</ValueAxis>
</yAxis>
<secondAxis>
<ValueAxis class="com.fr.chart.chartattr.ValueAxis">
<ValueAxisAttr201108 alignZeroValue="false"/>
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="1"/>
<newLineColor mainGridColor="-4144960" lineColor="-5197648"/>
<AxisPosition value="4"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</ValueAxis>
</secondAxis>
<CateAttr isStacked="false"/>
<BarAttr isHorizontal="false" overlap="-0.25" interval="1.0"/>
<Bar2DAttr isSimulation3D="false"/>
</CategoryPlot>
</Plot>
<ChartDefinition>
<NormalReportDataDefinition>
<Series>
<SeriesDefinition>
<SeriesName>
<O>
<![CDATA[金额]]></O>
</SeriesName>
<SeriesValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=G2:P2]]></Attributes>
</O>
</SeriesValue>
</SeriesDefinition>
</Series>
<Category>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=G1:P1]]></Attributes>
</O>
</Category>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
</NormalReportDataDefinition>
</ChartDefinition>
</Chart>
</Chart>
</O>
<PrivilegeControl/>
<HighlightList>
<Highlight class="com.fr.report.cell.cellattr.highlight.DefaultHighlight">
<Name>
<![CDATA[条件属性1]]></Name>
<Condition class="com.fr.data.condition.FormulaCondition">
<Formula>
<![CDATA[$type1=0]]></Formula>
</Condition>
<HighlightAction class="com.fr.report.cell.cellattr.highlight.RowHeightHighlightAction"/>
</Highlight>
</HighlightList>
<Expand/>
</C>
<C c="6" r="0">
<O>
<![CDATA[企业管理]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="7" r="0">
<O>
<![CDATA[市场]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="8" r="0">
<O>
<![CDATA[客户服务]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="9" r="0">
<O>
<![CDATA[销售]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="10" r="0">
<O>
<![CDATA[技术支持]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="11" r="0">
<O>
<![CDATA[产品开发]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="12" r="0">
<O>
<![CDATA[网络管理]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="13" r="0">
<O>
<![CDATA[设备维护]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="14" r="0">
<O>
<![CDATA[财务]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="15" r="0">
<O>
<![CDATA[人力资源]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="6" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="企业管理（部门负责人）"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="7" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="市场"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="8" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="客户服务"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="9" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="销售"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="10" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="销售与客服支撑"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="11" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="新产品开发"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="12" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="网络管理"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="13" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="设备维护"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="14" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="财务"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="15" r="1">
<O t="DSColumn">
<Attributes dsName="ds5" columnName="人力资源"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="0" r="11" cs="6" rs="13">
<O t="CC">
<LayoutAttr selectedIndex="0"/>
<Chart name="默认">
<Chart class="com.fr.chart.chartattr.Chart">
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<ChartAttr isJSDraw="true"/>
<Title>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<O>
<![CDATA[新建图表标题]]></O>
<TextAttr>
<Attr alignText="0">
<FRFont name="微软雅黑" style="0" size="88"/>
</Attr>
</TextAttr>
<TitleVisible value="true" position="0"/>
</Title>
<Plot class="com.fr.chart.chartattr.Bar2DPlot">
<CategoryPlot>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isNullValueBreak="true" autoRefreshPerSecond="-1" seriesDragEnable="false" plotStyle="0"/>
<newHotTooltipStyle>
<AttrContents>
<Attr showLine="false" position="1" seriesLabel="${SERIES}${BR}${CATEGORY}${BR}${VALUE}"/>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##]]></Format>
<PercentFormat>
<Format class="com.fr.base.CoreDecimalFormat">
<![CDATA[#.##%]]></Format>
</PercentFormat>
</AttrContents>
</newHotTooltipStyle>
<ConditionCollection>
<DefaultAttr class="com.fr.chart.chartglyph.ConditionAttr">
<ConditionAttr name=""/>
</DefaultAttr>
</ConditionCollection>
<Legend>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="0" isRoundBorder="false"/>
<newColor borderColor="-6908266"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr position="4" visible="false"/>
<FRFont name="微软雅黑" style="0" size="72"/>
</Legend>
<DataSheet>
<GI>
<AttrBackground>
<Background name="NullBackground"/>
</AttrBackground>
<AttrBorder>
<Attr lineStyle="1" isRoundBorder="false"/>
<newColor borderColor="-16777216"/>
</AttrBorder>
<AttrAlpha>
<Attr alpha="1.0"/>
</AttrAlpha>
</GI>
<Attr isVisible="false"/>
</DataSheet>
<newPlotFillStyle>
<AttrFillStyle>
<AFStyle colorStyle="0"/>
<FillStyleName fillStyleName=""/>
</AttrFillStyle>
</newPlotFillStyle>
<RectanglePlotAttr interactiveAxisTooltip="false"/>
<xAxis>
<CategoryAxis class="com.fr.chart.chartattr.CategoryAxis">
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="0"/>
<newLineColor mainGridColor="-4144960" lineColor="-5197648"/>
<AxisPosition value="3"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Microsoft YaHei" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</CategoryAxis>
</xAxis>
<yAxis>
<ValueAxis class="com.fr.chart.chartattr.ValueAxis">
<ValueAxisAttr201108 alignZeroValue="false"/>
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="1"/>
<newLineColor mainGridColor="-263173" lineColor="-5197648"/>
<AxisPosition value="2"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</ValueAxis>
</yAxis>
<secondAxis>
<ValueAxis class="com.fr.chart.chartattr.ValueAxis">
<ValueAxisAttr201108 alignZeroValue="false"/>
<newAxisAttr isShowAxisLabel="true"/>
<AxisLineStyle AxisStyle="1" MainGridStyle="1"/>
<newLineColor mainGridColor="-4144960" lineColor="-5197648"/>
<AxisPosition value="4"/>
<TickLine201106 type="2" secType="0"/>
<ArrowShow arrowShow="false"/>
<TextAttr>
<Attr alignText="0">
<FRFont name="Century Gothic" style="0" size="72"/>
</Attr>
</TextAttr>
<AxisLabelCount value="=0"/>
<AxisRange/>
<AxisUnit201106 isCustomMainUnit="false" isCustomSecUnit="false" mainUnit="=0" secUnit="=0"/>
<ZoomAxisAttr isZoom="false"/>
<axisReversed axisReversed="false"/>
</ValueAxis>
</secondAxis>
<CateAttr isStacked="false"/>
<BarAttr isHorizontal="false" overlap="-0.25" interval="1.0"/>
<Bar2DAttr isSimulation3D="false"/>
</CategoryPlot>
</Plot>
<ChartDefinition>
<NormalReportDataDefinition>
<Series>
<SeriesDefinition>
<SeriesName>
<O>
<![CDATA[金额]]></O>
</SeriesName>
<SeriesValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=G13:P13]]></Attributes>
</O>
</SeriesValue>
</SeriesDefinition>
</Series>
<Category>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=G12:P12]]></Attributes>
</O>
</Category>
<Top topCate="-1" topValue="-1" isDiscardOtherCate="false" isDiscardOtherSeries="false" isDiscardNullCate="false" isDiscardNullSeries="false"/>
</NormalReportDataDefinition>
</ChartDefinition>
</Chart>
</Chart>
</O>
<PrivilegeControl/>
<HighlightList>
<Highlight class="com.fr.report.cell.cellattr.highlight.DefaultHighlight">
<Name>
<![CDATA[条件属性1]]></Name>
<Condition class="com.fr.data.condition.FormulaCondition">
<Formula>
<![CDATA[type1=1]]></Formula>
</Condition>
<HighlightAction class="com.fr.report.cell.cellattr.highlight.RowHeightHighlightAction"/>
</Highlight>
</HighlightList>
<Expand/>
</C>
<C c="6" r="11">
<O>
<![CDATA[一岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="7" r="11">
<O>
<![CDATA[二岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="8" r="11">
<O>
<![CDATA[三岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="9" r="11">
<O>
<![CDATA[四岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="10" r="11">
<O>
<![CDATA[五岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="11" r="11">
<O>
<![CDATA[六岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="12" r="11">
<O>
<![CDATA[七岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="13" r="11">
<O>
<![CDATA[八岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="14" r="11">
<O>
<![CDATA[九岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="15" r="11">
<O>
<![CDATA[十岗]]></O>
<PrivilegeControl/>
<Expand/>
</C>
<C c="6" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="一岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="7" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="二岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="8" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="三岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="9" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="四岗"/>
<Complex/>
<RG class="com.fr.report.cell.cellattr.core.group.FunctionGrouper"/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="10" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="五岗"/>
<Complex/>
<RG class="com.fr.report.cell.cellattr.core.group.FunctionGrouper"/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="11" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="六岗"/>
<Complex/>
<RG class="com.fr.report.cell.cellattr.core.group.FunctionGrouper"/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="12" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="七岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="13" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="八岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="14" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="九岗"/>
<Complex/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
<C c="15" r="12">
<O t="DSColumn">
<Attributes dsName="ds6" columnName="十岗"/>
<Complex/>
<RG class="com.fr.report.cell.cellattr.core.group.FunctionGrouper"/>
<Parameters/>
</O>
<PrivilegeControl/>
<Expand dir="0"/>
</C>
</CellElementList>
<ReportAttrSet>
<ReportSettings headerHeight="0" footerHeight="0">
<PaperSetting/>
</ReportSettings>
</ReportAttrSet>
</FormElementCase>
<StyleList/>
<showToolbar showtoolbar="false"/>
<IM>
<![CDATA[V0dJMPjS5FZ[L9i;3)toO^p+3KR#%?p+/!5@qi3e=<b':8[9_eN"AT)`f(fR9:0PK8IUSFp?
Z#Wm<@Bm=J"F5qu-K`lOU1(pN9id3U_%7qj_^8\uMmDDtfCM^\mZG5.)n5HGBCKqoN2oq6bG
0Dd^NELo0Jk^RW'=VKC5$XaR:'2)KWfBOXSLkD=6"Yl*!"l1,FcZQIMIF[q`HSG;:[)K^.u5
^>T+?j^A0lAFb3K#-ei&KXPo.qJuf_5K!&[600nYGugk@.00A./]A@U5)eq@jAgX[b$3iQ^Xe
b?h/F_<IGMDB[]AcNAP1Se`G?7G<:,;S!EbEutr1G0-$dMM'o0GS4hnpUL4SF3tXC5&MknfB9
lLG_iSj@A2MAbS;7YoKAeGfTIQtsonoFF7FhN@!Rc&7H@$:[A/h50%P.enSU1:NfLIoi^?EW
fmZ#Il5k>\7S[<SU#1Q/Kl$(WEm0,d24,d`epB62Lo1=bF%Yq/nfU#cWXbeWIQkjOB`Lqj:/
<b'TlPR]Ah!RSJ'NHB4aab>'[_A==&?]A3V9(]Ab6<_)-IK'!5lMY-M6`^TW_a/fo,at]A/F-Cu4
EI&E(E7sd^ghdd/+2NXUP>eQQCjW@\<U@'"f'n!YTb&d-b"::5(.M->mm1f;&,45Ea_+Wd63
dFOf_<?>C>.6Q14d"_<LkI"Btts^$@UU@fIRI,;4:b&8k6RDi?k%qSgc##<fhC9]Ab+/[bokK
il:,j)-6,1;99)bg]AB!uV7*d)HQQ:<f<sN5>$*:Ii*M%8TlGM5h,ICb'`[tJ_U`5bcR#VE%.
OB"#7nL?XcUZ%/=-&=GHYN#2'bBd;4ao0SdGI,rO`rf;OMW%ei9ku&G:)drAOuN)qORDcl+X
8]A"=3t]A/f<J*()"uV,r*ALee[J`6i`!R(t4>rUF.o5ZL-i63B]AANK"5.R0Z#O4hu4B78on0j
F82Y1=\'GZlYtOd'P@Jdqf\X%LNW?*D5OB*B#Uug'PV"Oe4SCR\oMG5\iD5bVbWLU<4TT*+3
Q6c;,lEa,N-M5k4D3MrdCIi*@"#%$Ztn[fWo"pNjefoe#XX_,He&mHuJ4;g;9EQq>-MQIi]A=
1N+'s'lD:d6m#!5?cCbupo`iuVhn2,pDQ)kX[lc>#ep@sC7\_.U:.'0\Ro@oUkoK@bM-7tkq
/nthL?uAIsH*f:F3A?ePIGK7bl4c=\C)s=4>#l^mE6Q[9A00[n3"*H_ENoEsHe-APs9:Y;9_
jXCgp,mG8R<F;7=8WKNDjC9aMVF1GiM^8_GloVkTXq2Tf]AZ@gK-d:,4Q2B?U@:I\-^q6@/m3
gT]AAo,!j#E1,-npiBck!M\cTpdMq<g_X3cbC^:V8Y,\&<Z2,(+.lk!2_)m>A:"b2-2YuK;Oo
o*#A5Eq?tcAhJ\:[es85(5OPi\MP=/-!\<#QhgD.$k%k""H\l\-]Aq(57!h*]AZd$pDP(]Apo]AV
5C\2N3I:gYJTH,iO6)680D%B9SKP,>8[TEX,gWq9=k\<5jm:i2_M7'kM%\(/g,(kr`Bk?c^0
#T8)QA-=bL/]AQ_W"j1Qn@QcAUU)lg>ZM'9<2K<N-Jj/P7biViN,A&Dr'lo<peu]AluCHO+TqW
@-99L#FuKLd44Kci>n%/L>@!]A"X`R*?ARR`69JfgL<j&t4lgL-^:X;)p<HT(&#)pYb%sS,#j
#p6X'(`4RLTk7qEbG]ANs7/F`M5'f@377r9po;Tk\UM3OIA?JQ,*,l5'E`)b2u\FcA@l\g,I]A
e(SZUC#>q=c#p@\A?6(!bcA*tRu!h(>e%HIA(rJ)bFSk%ARHu3F,K>BARSTHW^]AMaZ>`.e3]A
VG?hZh&e?h=]As]AG(VH)`B"G)#WdN#"oC:7*3Gc`&\Zie';iQKM%T*,W]AnbDE893D1=o$!/ea
Pt@]ACp1=cIiL#*5uafrm)MBX0&qOE*ZbXT-NCb-gZWF^:uV3[S1"7SeMd`_,ld9H9uZ>3srW
ek:1b)j^33Yh!=%f(pefG)\1Y4fmiWJO(P?V%e^8DKej5b<'28o^_2>uh%n"Jh>S_UoM\/6[
]A)Z%8g+ba^?B)K^YoL+I,fWilD[1s?tJpfJk=)W#CH!\q%?8YHS(:l:*p%(&PV%SA(2Yp6B\
/3^E#3DfkK*=]A"\ic^@GNCcQSFACMfJ^A(9k]AB?grPKgIS#4*:5`3#!i'.dQWRHGF]Ab3l`!*
]A^HB4P.[Vm\4?pL)Dmq#NRbmCm>1[b.tHKR(@>1T2^$TVk]A!._cQ2tj)[DV8HUj=)6@Ih+Fc
u3KqnL,f4/r1knW@/\qZi%Lc\gJ-2o)+C"ca9SHM@:'gU$:m4lnu"P*TF,(V4.j(+-(+pr\\
Ih>UOZ53ng#q#ZCO2-uoUUBCX#`6=)HJU%>N&NCl:b^/oo@r%rMPO2,9^W&8I;W1W2`+VmlZ
L8oK?&9Xr0;U&P@$k%H<_J9Hn=9(g6$431GFPC[)Zeu9UJStAaKR.:YCP-Pe:VI15CYN?Yrk
p/mlInHFO"rFoXSg_4A2cX>"`ShmH.@]AO.hg#1nF,j;9.pegU`YY>TDX;CSCtA`C1Pg^,h@d
a/HNYAq^ATHs`Bq+Whi2Cd(nF`?oN#^h'<@ppTqELM.TVF%Qe>Y^KAq`!aoX<:;=1md5EJdr
AIHlaP2;S3.]A[!FR;4]AQrg*fDsjfr9=#G?qhlk*m'aS8t:,dLRhB@C0I7"\GY8ec;!DJEKT=
Q>s_U7<a'YRS2N.s!O1>Yi4S+E&(cH9&mQ".[`-]AQ_F9u:_&u?KJY.?s*W>!f2cV/-W6'I>G
C@']AL#IjZc+H_G?.4831Ot_">2jL\ND41QL_BZi;Amf\5R=$<+Cu2APllE>Yq^KT&q+W*.YM
(s`o#.%*m<TVr*5Cml*]Ac:i6_i]AdcM/bLi@fu"9U4VD7$iADA:sXpi$?M?6DV:RD76LD]Acrg
[]A9b/d6kH8<DV"X<*Vn>8LA,s.q3O8gQj63]AJK\bY"adQ#gMsH?\=ld_GkGPrUF[=#YdF8RN
?T/co8tX1Dcq.ibN:D=;;X4(K5knn&fX*+S1>.Z^G6=//g1?T@uW*,g=B\e/7;Z:UaU,#j4#
qno/jlGU;`WHFb"/ooM*`KW5!>^2S"<[#;R,E5l/l0>'+S9#P,OUXg!3kRu<MrM1n9I["Lg`
,8YU]A!iRsp]AU0)R!34m2n`hP,*.0`pcuWe1PXSEll\T5:i24-)eK8k*^8+L.oRn0Q3]AlFQQA
Sa)Z5Aph!*JQ'BSeE>M]AoP/e@SH=%NZbJs\k]A,rK2A!>+`lq]A3=q`3\.p]AKP6a:^h<K"LG8T
2[7OhY.@H$R,`q$'K=mOP$,G#]AribHp^E+2-f<bdp/Q47!`3rp7T:B'Gu6lI>X2j^BoI`^-#
+>2a8E%R*'cLoSFId!2Pe^Y?\u*`W]AV,VPO5X/BdR)ro_";t22KXqC=NS5bRl9CK7UrMV<n7
aU<t$l&<U8rMp2M>O%l-=NOReNN6+l-2pjX-=7/nN@h`uOTbCCJ=3H`e&2>S:Phc^U4n=`si
;.c%h!./44pr_n+]A2gL1]A?Y+:qu)h8^r.=b0,9&l]A7e$O5HW`9n-i<:F]AhRG-Dn$.uCjdUR!
?3K8mhlMrEUJ^RjcEV+u>)T(Ms:f!;`EG4r8ML_JK#8)2dsOFS2=;-+nUYDtWa85!0u6b:6T
I:tu;CW2jV4Z>umE2YI%pYC*Q5s$45mB-;p8b$IC.B,#h$RD8g`MVN.1KdMp2')fTk]A3lD8l
k>Q*h^Wp?Pl8W!;-85(&^/AqC[mOU).UUJ4fd4d2kD\Z?`*PQQif$pX4D.ls<E7j/fmoPWql
2&BQ^MM+2QqA),/k2k04M_)G>BM8Z3dn1.J3$OYgnd"X@fE<%L=\Rg@JfF'6!-aOknka^MI]A
N!V!&Y_*d[S%mG]A@OGTgi]Ai6m&o,GHQ[R?Mh.OoC4s(*pO[gWl^sTahTH$*N3sarFtaAu)4Z
8gYl:-583@18qi774K>dqMOk)X:Xftc_#VhMjcjk^2EE%(aqUVq,/lR2sK=-NPVU>7=[/CU?
3JF]AVr`&lRorN[$"_u%l/ki8lQKVBFU>05=dGf2_P973k^*Yq@dthaib!W73]AI(bkD^6:(*,
KHTGB]Aj_cSYrfZt8buUT^L4,j:Bk9p>FakT,o"dXk)K]A6u$\JcQ>kK"V^fp$ko)1Y4j5QN^>
1U<A'6BRHG+/Vp0VBs1q42YDmCdu%*R\q%RJTRDcTPgR1#PnW8p4%]A0G9[fRf3Vor/@"EM,@
#]Ar/;?`r]AP!ZYONkW4!a[?Ip0NiO4'7&L`J81qnB<s0hIcV^A-8AjPVD*EQ;44CH?;ue1MK3
,fcZ[B2LEsPga%:sVaahF5(=o'>p6_UPRq151F)D)BlBb$HaO8hM9Umt5`T18Ld9o:>hB(`7
Z2Y7$VAQmuU=$9?=%JfOg<GZs]A+B7!b=`t2Nh*^`Bef1l`"15ko-9[VV7AI;#B%[1N&S/DSD
41HN_:WPo#`cphbqnNM(;p`dU%\,<s[cVVtp7`i/e=%Vic!+O6=Z0?t1Q5n?fR/A7MqcR454
?LZ@Xp+=t)p$?uctGhNa*oV%-5d0ir[B7.r`%X9X%AaML/nom`&o(M%5)K*l<ITu@>!M1$Gf
$V&HC4'8+br`3MPBKaX/jCbWXnhV5Mr`rjpblE2CHHK*WNj6H/4n<MD\$RdKmUK[9Dd@EdRD
(mKthL**$@XQrXBb#gUDB]A>LEQ,42k/A/VQ1rKjS%i[d"9<6W\s5`1,*g^*1:@IM<oTf>p/4
n%PE`D[lnf'3u[-^NK3P1u4!*rBhW"oN_5QF>1O'GiWmQ"+;UAFm=O681c*<3dcXq14V@IIu
O!+j[-F,CFbXB<2$^8H8^TGjo+f>;O<tE"XL'lAU)&cfS(^:ec(79$m,L&G6!73c\4fj\7gs
<>F[9]A)uhYQ=.F.d.uIkP2aIFTOVYabJ8S^)/VJ!>[Z-N@$b$jMD7NcXqDD0_N=jXH)!lrr7
OVi%h^(kqqDZlB6+k!8a6QJEE(E_2>GWhtKIV9ABKrs2a9A@(9`jE;O)L524!/3[Gol+Ui96
GcbVJSRqBi9[[7(KjU/[h,26AL(;32Q>cN$_6m:hs=-1=$F]AJlUrq5G5TX$,=A/`$DRoWjVa
WADi%Frc+Y.Hs><MFY9tAAtCr020W*lLEu/n<JDXoDOP@%WbcV?@>CHeh<(+A=sMs9+"HSr=
JA`ZVp[_:^[gP\^&a;/ZaXrj$?"AHDc?8nD5'`;B6[UL28h#Wf8)]AiV]A6W,#P)%Fdjsm?D7+
58t7_djYGX)LNh3!PhIGc\W`sr+%+N+e#U5(";1%j[4bNEcOc@#%9qd0a[RBOT+((1!UXMq>
qAJ1G)6^WG@42I9Va^,";^]ABr!?Yo^cHZ%3Qh=cTXE<0BTE_6X'=0j=sfSY>Yh*2bZ6I*B:*
4O7FG&ET>7(k0E)7[L<>jic9bTm/@FVXVY&lAD:_eEk-\Z5Y[rBTfCc>Lqk%MRq\YH=loXd)
fn$K`EXAml8m?h=%"JnE(SU8G5fn04b"4[&pLGdL!.A!qQ"ijcClhQ&D(G4M6K.r'e?gh,J"
l)[T[6%_pX1I*^<U$VeG[,SO68gu4(6/<IOdf)<9VVc;:F2:f/rUq'!R/Boij8j*_m5=A'=%
ca55-"Fjln1A"iiXQZ=_pMIk,8B[R'bL]A/W4]AUr!F:O\3.o59PHs%W$QiBn.kA-Vhm(7.9l&
oY3uNSce(WA-k"C%E4J\csl#[scZF(i\@alGpT#6uDNClrQcj]AnZ]AW9SP84SkOh;Ll0qa1Aj
"RU0TuSHJ?"#jlX')%!LrfeMToU:tcL#&$gJt!Oe\8OG_E&V(=KeTYBP.I'[`u1&WHs7`>rZ
:gK.O>:hE/UU3XeVNMb.>1#W<LV=HQODmU_$Tc_gRYU<[$08#o_2>:8d[dkf>TH-X!q1;(\8
__TL@f-;EqdbOh(Yr8)gLJH$H)60Q)N(CJ:FJON4]AXe<f<'%9hu;4Ei0ZlGWn8R(2lrikUO;
Dp;fJb`T7)O)-Lq^GPTWm:'H]AN.#0"5Bnr(>*m8'(%`O>.B5`4rTq^!24e>\a^=*)Koae!9(
*Zde]Af\5s_mB)I)"=o6Se;]ATX;Va<)X\%oe=F$Ron)4jFZV`6MEom-$o3#O2h>p$\&q3"-9C
#0huE@nf<KK,K+#tgI`WpSh>T>[=]Ag_*2:,mjV7s-l't2P0Y;f-;V+1PSb.NG"O=oG`eU`6[
rt%7JVeZ3`V_fCt`uB7=Vl-FYC^/\-4pdN3l8nm7Pt+^N[*cF<P(IbidmFKiTqJ&dV@hr`-!
D-<,72A[F+D34Q3SG;YYnP%:tmQX*>TDK<G&PK#e'oEJ4NT1hL(TWD)K^DoU[Q#rNf'1\5XT
J]A@QO31^=p30%bZsqil+M5(@PphPrc"?*:^QO"*Lt,OQRYHucF6]A<)PTZe$R#epY-umB&u"M
!K]A7Z^Z?L0eWN4nL-GIi;a/i5(>NoLD;O+Yia(X6XK2JG\!60[5bkci$i1!HDq`;+'ImuIQe
gN=UJ'^UnHg,\=tJ=hL"GA-D*Y_-6.i[s(F2-nZ5'a-4KrPKK1<(Np#0*&aG:g!g3qVOBT2d
MKSL]A_8(?4#Zmc<kDH\/#dIF3R20]Ao!AhdTWHi]A60h>6gY0j[/ZS0Q\J)$AWFkH=l:1N\V"0
)1<lmNWOgjH>4oUZ)<UJViD?nfk.Fk(.X*j<\DXkrrcjmH1@a6_oNcHOANW8Q?%*W?q="@**
W@e_Y]ARkkk.-WNMEZ[!Y';\LaMcf-Fn&G*!S@ih#3A_kg2!^5VA>Vu+0%[j_D$dkd,*HY+cD
PE&ZRX/^jQaas.&-"U"2K\"A>HPP-UqV?OM2t0f7:XrolMsTSkq2^`M-Q+S(FHaul*[)gr.(
SA?e&+oV5ihP#2-h*YFTgqV+$:q]A=T_"`0_O-e$9Zpr9hUO$7(/QT:Z(=%YsEG?_nM:PGkID
9Z5^"VV-qtM[E"[1FqWrf'eP!%)&+En`&nAl;Hem1%H#3YD#Eo`+<*nKYb,:_#G;7=eG7"8r
;Cf>GP%0iT6+HVKN!G=#pp\7S1b9?R'4kVPrP@^b+K:,uOYDHp[kW)U:4f5WrCK(ts^*/4,o
AJ.uPd-P.rTCW293raI,]ArfNpc4,b5_6_mC2b*^u%4(9)pAPnq."Qo3J+ErUf'Q1o.[G1#1e
d-C3L)]A/Z:iT$%4[QNIMh=iP9Vnof*W:2$UE"^1oNC*GU\/;\>2X2aho5JU!9I8uUr<CAe//
P^dqHF&o^,D8!?$uTg-;4>E-lK8_OWq4rhQDkN<N7R,lrts^rn1p^#5OfXFt]Ao2Q_4eU@$mo
=BKFaKQ">LQfbWeBu53q&b2ZlaFOhq^#aKKRZW@DhFhg:5Mg2F>CrD)IkW\PUpM*#X]A62*<B
[m@Y;<qs4.o<dGbO)+R-MkF:`f2kg2bEq99Il1+WTfh,qR+W?;:/R6]AE^cY<_KtkA[&c59K:
aSW^^%:=l8WYI:8<gZ.;+S*"On]AU(h[/o7=&WuNn(QUgRH/[flnMe:r50[BEKgs7Ec1kAjDA
JZ.k1XlshmCcQ?UY*?M3N_dP"7[oc@`g"0iCrpnZSNl=AZ*\k=nY:jrc$ZlGC=N%1-AgiUHq
n&e$6X'CKbGR*KF]A.17dSSl"u4u9lT:3EHGb4@caJdHiHaj7^VNWf5VZ*PBpQKaKsPK"f#^J
,/Uj!4FXr9/FoTjO9DqMo<"Jp.6,s7.3%YM3A)P;^Nn.TN\Oa"Shu*fQnFCs^Qrk(i$4*3Lu
PBl,>2Z-id@OMk.Y'F]A/ZLm]A(Q1uiKe)HM8NtE[a()g^[tKelY!BV&C-_kI,Ro;Kp=h&nnUS
T=+m-hTmEm=)qmss6FE6*>A#UMN5Rs7)iG_1@saW<4#8in0UN]A4Z?NdM)]A`FRJ!6CM;uMX#5
jY7o>\fr>EcXu:B,+0#?"V(Es&t=dm08Hj1@\U_\.E/Q37MqDga%4h9(GMs:7L5hdU,sWgJ]A
cAqs`99#O@)?V%MTQIbriidc>6!qGF**\Z["OZIr,7/rc:S.lr&WGi<D20@1LT:+i.,N(Inf
lRU6l8GmMRhUlFO_'7J+H7Z\59n*UfDl)H0$RsP9Pnd=Zn@$c#ZLkTa\Hea!*A8n0mSM?NSl
1Pga02iglb3igAu+?I&.`k]AB9Z1pW^@WEFKLM-qOC\_oX&o%UD6EIV,S(Xc=*cu/!D/O*\4f
T#=QdnI4Kp$T1[ej?7f&<jt9['Pmih.]A#r(69\ATHM"f_d"T0NE4@p"mFVt%PKZ`=fgts=&J
i'gC.J&&VS;"XC1(_aA460Q(,9`.bA%R2W`$+]A*]A5_`mY9*8kRDYIh?LV`PedkCL`U5]AfB[r
X,BPTCO$4K3fHrUbM_MTsQ^A@Q5\KR%#dBbbD=-ElQHnR%<AP9sTo0?]Ad_F]Ai-r0_X)HHDN%
6p+)^]AU8?hmQWo<?s^_jjFt$6LG,eU;22nkjo+G,h:$kQAN829bF9K?lH608lg$h;6KmIId[
p=<-RliQ:*.uF[j9YipZM.L0?!i<$O(M%^&OY4kH+dR;d((-B,^PX#$*n#^%L8c!Rn8hm.>#
CEpdu6%:M8?NNMus??%Wtq.84,&)4'[j?SrQ;R*=9j&Ah+ISL3]Ap33iACQ@^T06)h4*i_GcP
TKJk<(X\2B;XB\^`aT6$V<J2MH:3VL55p\I!N?EBpLtLnY[Ii-@*%bK0W`^e5+=G.JR&qB"%
.*"LG=^h`eAsp_P3&Ko!\YDpepOfAECF3Y2*"r#Lfc'DQ_EGZm"Mbm_N<Gn6h-28sS`2^`Ag
'StkY7HkLVba(F(-^_'nn^;2q1\#Ug!q7c[+`N[ja.Bu@OZ*C,TJ3[ZnfflRM_U;&I6F,9_.
U`-0g5-]A&KVgnaV+BuXt,YhliM,e%g)[HJ>\L$Y/9#k=N;^rDR<e]AW>mViTO187HC"-(#(s\
>X*6;Rk\]AlLN^LAg*S=/ShNFBgpm7o^=iscbf7WZ9GN!0a-@FG&IV\PTj:3WuA.Vt%lkBsnM
jg]AHj"E^^.(fZSWQkbulBb'd:QQ":%$Jm7`#g;i4A=)H0:Uet.[;oYee!Vr"77k*T4]AL+.FE
[Lb<Rma0SRg6Fe6sS?SGY;`BXQK,$t)qa]Ak(h.EHf=9DiRD.+)@pJ(l*5_R/oopd!RS8g"5`
;P'CND(5b6oA'WRosZrE&W?bclWF(G;]ADo<3\5*Z9CM[ML%pB_+HR93f1uofUfo*f2*_hJe0
(."mVpL>AEpe\R+kULaR."iQ@EMte>.;4\Wr"U1Erkr;_7o1G6HcD_Ya&l;I`Kb41fqGDZ4,
V]A=hb\b\OY=_j@.cF34W^9a9BL^u069)st6Hgl/d_6nDXeac#VGhE^)mF>a&%*WA-8bo!,q9
4'\WLQ=:^H]AVo`CBg<J^@9DJE+STqiJ!U@8^O:Me:?]AUd;+2XMZ^mSm8g[=`!p\mF&ED.g/X
]A$KOuhAee>!D*Z:5nkt&HuOjMgS2dL#WhlqrI"Z@I1T0btOa2I1(DFIcSmlUtu%\PX*L\Qom
8GO*K,p]A'IjeQPe`XW4$YE:G/LKm^R=9l2S99mkRYU9W#iu3LgT1V@HV_K>5(hfPDJ9;<#-u
DClZBHjpa8)bg>qMf5_D=[`Td#Y-MHh(!0ro`*Y.#<HQ5X(`*=1DPCo^!4%2*d?<&*WhUPlN
+9d#4)E#fF"lqF$X9#NR_d"Fa3,<)7=S'ksVPj?_BM%e=k0Ujs63hUG7/>.*(Fbi:Jk:/9sm
JZgc^H\_WfQ@i[4Oah-;gbN5H'8P##Eb>4]A,n`sAYiG;*1gsRD_)gQ_ILgX-1%_.>S0+]A>kH
Ul5CQO#L^OtCW\=r3E1*.EaspR^*GD,YbI^QqmR1.YoV@NecQKpP_Sa'^d2hO'BfelKCqg_q
*f.Wcm:2RXRX=ZTX."4:AIBLMO[G,Y5@3nH/h4Z2.D7WT2<':b-!d7pMrDI$Up;G9Xlq6-*J
&nbAopLQ6`j@;jU_f-Mi,;oQW^`'a]AN@nNGMqMUF/UUH([U6R,&FiEhXOYjT_?VL^oZ/1G(a
[Mfe#NZ-PbG'GU?"rN4-l;dpX.XQAdNaC,>rctUCbMg+QQPXuGJKH*BV2.@R)3aV([0a2lYV
S$2Wkl0FScq0nS+4)FHPGnG-J4;7m0,0E6pH;(u/3lR!I_*`1kR61&R'V*UFnj?OiRNc[Tab
Y19[Z\o[@\ls?XB:E-VkrMQ$[sW<5*m<j"LkI]ADt^%epmu#18Dbg8?`qJKE#-IU5OoM5T.US
'd*9-kb'qrrr!.ON4g!(A]AZ9$rKJXc#cM4-%d^p9/?h,L+.eD.bp*WRGJWsY_s)B>Hg'G?BY
<lp%JRh*lO,cd(B#hK%YY_.$/^[E&^$_119QamKMi8@'4DWmhe:!:%5Sej71#bU"G,ERq/Q>
`T:'g>=Um<'a.D-D0CS!75_YTJ@L'5LSE`sq7.*m<DH"tWG3)Ej?E1m)W[m5aFZKc4qQ)mWh
-JEL_l)<<;F^W<B(G&%$55/Z751>KU1V!l96?2lj[ep7U/k^;\6&\OaaKJ^U*a0S]A;cuJ47C
A2'6>^>VZ7_W&!JI!>.k*=W-d*7+Bn)6Sj]A0$V6V\P7sV,'YUNbeI1#R&f?CV+jsR-=&j!Q]A
qCt\@qMiS!A'OJ+\61`lS#6Oe&fc,*D]AN;g8b]AE'22H'!pZ%a>,S8Z>`)DGH30I'Sfq%YZnA
!+O$r?&<5o5Z<_K0OA'@:o0VKZmJ3NH0PL3L%?,tS)*es(HcZ#M"S&Jc6aduD_q364R6gfam
9caQd9hp3)YaK1-u-Ejt<cK'hDKeZuHJQoC]A2*r_1du2-HAjfs4lgJ"a]A*3SRYY]A(-b@ns:&
im3m7WSLMjuPgmeD93G7!k3R\`aOTYGJLe?("h*]A>'Zi*ge?s@5.OhjJa/hre&Q2K,;Y8\c&
*QJ1[g8/n38f.KT=*VSk,0[R^tY>)+45F)Fu%JK9h<Npt$k\AT6o6^a9iTblTWi/3ErP6rY8
I24(,QqF8%la:H+TN[#.Q?V?;N]A.(8o@Sbsoln<uKSSq&G>=M5:UT;rHptmbf@5+elr9+d$)
DlCZ)Nnf0$!"3<ZkV&UNrQd/(EB)^i.OsOR(<QN2!q'@lpZ6Ug-p)jfsRd?(j7@2RJAsWO+0
DdLr)]A\Dm4NU"=[T/dXp]AbA8(l>IPA#eJcgf=%S7_CL[RdX4I04`m`RjO[!;uQ5MP`_G)T\)
rDlXf^._OW]A]A-J"XD())R;*(-^4hEcMAMNFo$%'h5F3ADCba*;t9R-3)5#"@0h9T(s>Su.=e
8->?T+:3/QqoS27$g3%m4!7U-*n6i/4)[NGtT)GS?Mnna$94iUneT?(>fH7Hcj_t*/k&cBgB
D?6C*QA89:s,AN#.>Mbe>:i4<d`-F)gVGG@C`41O.uP<N'YJ9+okPhJ"!ZKt]Ad.&<%+M:]AT?
5C$I%)qt4u>6$:eGI_3IsNe/?>sQfNL(3\#;eZ+L5dC'JK@E"5!1"]A27]A!r1,GrZu>19peY`
_p!S6YVEW!\'Mb&.S]A5]Ae+8Ygo_1D'G/)R&+<S.[rB_J(%;R;7Ida$\fn.,WN06TA1?A4*^n
%&Z(,Wpr:?.mc9Xa>`=q\>T$qJ?a&/>c(bk3K[l[?t.s^>X)/553)uCJkWWDp)/4dbm\!MG8
)-oDISr8k@`%%)b[23e'Tp5Jc*&NBc?j^=YnqDrb8lq7,6dX4FYdiW\\jhRY$S-!o^Q?%Y1m
e>93L+*3ooGYVq.P?0AK]A5I)2\6L2*%+D-\H'\pSZe]AHtr=ELZW&N>s%4!Q2%P7EPWfe:)'a
)?"7!O*6d++YMW,Pl/MaB'E9+,TP=aI\"GM5C94?G7N]AO7EnYi.R$>"I!Yj+*\"s3Et0+(7V
`7VPkYCuhG(_:$,9^/$h1l;C:S=*IUC3+s#?S!Yuclg9j(N^6TSc:-/t/J?6?0AuG"`S;`99
"KggNHB=:[A(,e_'$!h-":hLm9<3p.@J85D4tTD(6s)J>`"EUf=c6gfX\-S0kLi*<ElA$rGR
*9QNu_.:<>%0c.3*W=$\HZM:+0%\%N;s/fV]Ah]A(e`c3:2iHn%Pn)UuF@ne&14jEQL/i6$C'A
AE+5$kS/.uXXlIBpuKk(_D(C,m8!>adJrp05TJHMX2dUN-WP.MaAo#dg9$@7qu0"D<#p85.=
W!bRTV-.ZbUkM8m^V2M*48u.o&1Y05(2Sbd$c(CkV8Q[]A^,7!`3fEG5lE0=b+]AZn"l7T<k^u
Xc)0\6V/9\7J*2il5qmTML7mJ.FnbrVhm_:p@bb)hQ&iSR3SY[Cca*H07`U_\A+_&8OQQq:X
C=)G9Du>G<eu$S[A!I\i0CD9n@F1a0cAAURM;,D4Plpe]AWFt!GpJ*LPM/4H$B))LpHDseaUj
F@BLT9-fE-\B?+m?KjM,lnroIP<%7p-^,@el#Zp4C`6tBs^@h'&cl5s4>m9cXn3b/JYPd-dM
DKa%l8.Vl@&_d-55B:U(E3L5%8_ie!TKh14+k1+e]AN&W3:=L2[9[!6qr$Xfk;#.Apg6`2t@Z
WAm>h+WZ<i+b^:5rekA2@>G*R:VRe^ii(pX=[]AM4'/%bX/6J0VXe\@6P^C#<*bC9kTht5on:
LNH([ArESd_dqBi'r,'j3/"LUB/ZRDH<>+QoUHB%TKt-.u.?A4iLe&M]AlC#HL^Jo2u0e4=]AW
R'%@Y,\DW'VuW&,@X,a2c==$Bma,*RouYFfIW;cd<gH639"bdD:2o8Z3^<]A]Ae4='o_Ii_3IB
fF(BFg4FH&s&3NOBelnM]AN>G_`tSYFoRU=/k.,F>Adr(5Y2JQo@TVIj8TMWu!VdO6VF-Q]ABB
]AI`5_4l,k=4@luKE%k&e><oLAqmjfsS`2^eoo1lc$1cQ,=$JeP,%0iJBk*?eM_EgprHS=Il`
6c:QujaFom_E(f\G`[V9ns%2Q>-gKj$'rH4uD$\i%biOqhe:T"0eB53KJ:-*/clo"ZO<g-(M
uUFQrBK>%J\YoFde,K5=p,q8+]A?V/\6(Xhpkg]Afd46Ie[lPrb[aeiKpKr#2.'QBOFI^4M1$0
AZ!Bh]A-4SX\oRpSkgEQfp2-!V5n":5dbpuD(."&eMsP(F3?"@>iFCV#4nH>j"C);0D;^^1j$
gM$l;0W\4(/-WVb*TpBsPiZXT:<#43$7_)aZ:o*%mJhiGfa4/mJ04.tsIe!Fg5mKT9,mD3@e
EQ&VW--3oZh7i=[iaIbqig6q[$Q!h,7_1^RP1r9K6<h[1bs]A3hZIC&I7Y'%Pk6K$-^mkSlM<
%6mW6)3>*&^d9A=>U$dhW/EgsA(EBeh$a\^1uio?*HVOW&8D\k4?D^qTH7;(iZSW#cu!rVeu
$Ku%G\,Oe`JW_GNTFLdA-T#bDsrL=LpUUMh(q[eOlQ^WBN*8j*Sp3\kXg/enN:_`7oEg0a"c
b>V;&Li$&jDL3#IR9<;butpOIlRsBS!*-?'_e,mk1653Os6&'4I>TK`G]A!NG54uOrI!3uCL?
Ar.5(=6jg2JF7#d_ZU=SC]A&M^5f\M?Fm*(rmS1*1UK4:lk;\)Sr`\f?(7,b\ahmU<ARa'His
r!Q'.gn1eZb`E\>7g(?nZ,7km`<,>GEJSmCI<d&6qF9Z4<SMhG2np7S49J,ep+a&B't;%]AL#
d]AH]A)/7(G;)m;VS95/4:oU(]A5_tj7%[I0*Vm1Rl^k[H$1_IJA/6XW!<7`]A:bMXRKkN%(V*ht
=18?LVaf0aPF[R#qPX,=0P">C"&*OJ/!*PjaFjeb=^WM[8Be_bKKRnkk]Ao9#:7T4Gc.4omMO
_>9m1!Sng%eO"T.5C>7D1S>R^FA[;fa'3C?4U5#'IJ9a]AQW;oJ9ZE^5%HtK?qHT4PUObkR.d
K^m71f$Sg3X+[D[n"!PsjfTRTGIp`BcOG!pNDRC\"&r$_E6F[>%JW[%leTm"DAf4iiI/l9l!
@!Dng@r&BkF87)=q:+'1)PLSS;1FD,mN/1("ciO-n)ac@8A&q/NCa:*.+'tg(J!SB]AZ[*+p[
)!_+['UTM.^Ik:78J0oaYK;GqJ@EE"uDCR'BKoHLXE7@f_Y2oP[F_Q'ISue.jQ,.i.js.gu%
#*MQ;5<\S]AS@#0XOBf?N9?G!I&`Pc#+?9OA]A(<@1*-5:;?M\nECA0/2or\rBl0\_=5?UA4Il
P-tu6;ANaIeT%#)lNGNM>0!`b^JF*S7Rn=mN;FDbOP?2;6b>"=W@uH@7ESUiGU-.Y^^'Z=3m
<?YPqW7chRbB;cD7J,$__Yi;Wc~
]]></IM>
</InnerWidget>
<BoundsAttr x="480" y="38" width="478" height="283"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=$p+"地区工资发放"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2368549"/>
</InnerWidget>
<BoundsAttr x="0" y="0" width="478" height="38"/>
</Widget>
<title class="com.fr.form.ui.Label">
<WidgetName name="title"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue>
<O t="Formula" class="Formula">
<Attributes>
<![CDATA[=$p+"地区工资发放"]]></Attributes>
</O>
</widgetValue>
<LabelAttr verticalcenter="true" textalign="2" autoline="true"/>
<FRFont name="微软雅黑" style="0" size="80"/>
<Background name="ColorBackground" color="-1"/>
<border style="1" color="-2368549"/>
</title>
<body class="com.fr.form.ui.ElementCaseEditor">
<WidgetName name="report0"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<Margin top="1" left="1" bottom="1" right="1"/>
<Border>
<border style="0" color="-723724" type="0" borderStyle="0"/>
<WidgetTitle>
<O>
<![CDATA[新建标题]]></O>
<FRFont name="SimSun" style="0" size="72"/>
<Position pos="0"/>
</WidgetTitle>
<Alpha alpha="1.0"/>
</Border>
<FormElementCase>
<ReportPageAttr>
<HR/>
<FR/>
<HC/>
<FC/>
</ReportPageAttr>
<ColumnPrivilegeControl/>
<RowPrivilegeControl/>
<RowHeight defaultValue="723900">
<![CDATA[723900,723900,723900,723900,723900,723900,723900,723900,723900,723900,723900]]></RowHeight>
<ColumnWidth defaultValue="2743200">
<![CDATA[2743200,2743200,2743200,2743200,2743200,2743200,2743200,2743200,2743200,2743200,2743200]]></ColumnWidth>
<CellElementList/>
<ReportAttrSet>
<ReportSettings headerHeight="0" footerHeight="0">
<PaperSetting/>
</ReportSettings>
</ReportAttrSet>
</FormElementCase>
<StyleList/>
<showToolbar showtoolbar="false"/>
</body>
</InnerWidget>
<BoundsAttr x="480" y="215" width="478" height="321"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="label1"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue/>
<LabelAttr verticalcenter="true" textalign="0" autoline="true"/>
<FRFont name="SimSun" style="0" size="72"/>
<border style="0" color="-723724"/>
</InnerWidget>
<BoundsAttr x="0" y="191" width="293" height="24"/>
</Widget>
<Widget class="com.fr.form.ui.container.WAbsoluteLayout$BoundsWidget">
<InnerWidget class="com.fr.form.ui.Label">
<WidgetName name="label2"/>
<WidgetAttr>
<PrivilegeControl/>
</WidgetAttr>
<widgetValue/>
<LabelAttr verticalcenter="true" textalign="0" autoline="true"/>
<FRFont name="SimSun" style="0" size="72"/>
<border style="0" color="-723724"/>
</InnerWidget>
<BoundsAttr x="480" y="191" width="300" height="24"/>
</Widget>
<Sorted sorted="false"/>
<WidgetZoomAttr compState="0"/>
<Size width="958" height="536"/>
<MobileWidgetList>
<Widget widgetName="label0"/>
<Widget widgetName="year"/>
<Widget widgetName="chart2"/>
<Widget widgetName="chart1"/>
<Widget widgetName="chart3"/>
<Widget widgetName="chart0"/>
<Widget widgetName="type"/>
<Widget widgetName="type1"/>
<Widget widgetName="chart4"/>
<Widget widgetName="report0"/>
</MobileWidgetList>
</Center>
</Layout>
<DesignerVersion DesignerVersion="HBB"/>
<PreviewType PreviewType="0"/>
</Form>
