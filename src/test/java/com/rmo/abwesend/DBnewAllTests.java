package com.rmo.abwesend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.rmo.abwesend.model.ConfigDataTest;
import com.rmo.abwesend.model.DeleteAllDataTest;
import com.rmo.abwesend.model.MatchDataTest;
import com.rmo.abwesend.model.SpielerDataDeleteTest;
import com.rmo.abwesend.model.SpielerDataTest;
import com.rmo.abwesend.model.SpielerTableauDataTest;
import com.rmo.abwesend.model.TableauDataDeleteTest;
import com.rmo.abwesend.model.TableauDataTest;
import com.rmo.abwesend.model.TennisDataBaseTest;
import com.rmo.abwesend.model.TraceDataTest;
import com.rmo.abwesend.util.ConfigTest;

@RunWith(Suite.class)
@SuiteClasses({
	TennisDataBaseTest.class,
	TestData.class,
	ConfigTest.class,
	ConfigDataTest.class,
	TraceDataTest.class,
	SpielerDataTest.class,
	TableauDataTest.class,
	MatchDataTest.class,
	SpielerTableauDataTest.class,
	SpielerDataDeleteTest.class,
	TableauDataDeleteTest.class,
	DeleteAllDataTest.class,
	})

public class DBnewAllTests {

}
