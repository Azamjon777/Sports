package aplicativo.para.esportes.game

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    enum class AnswerType {
        YES, // Для ответа "да"
        NO   // Для ответа "нет"
    }

    data class Question(val text: String, val answerType: AnswerType)

    private val questions = listOf(
        Question("Is football played with a round ball?", AnswerType.YES),
        Question(
            "Are throw-ins used to restart play after the ball goes out of bounds?",
            AnswerType.YES
        ),
        Question(
            "Is the team with the most goals at the end of the match the winner?",
            AnswerType.YES
        ),
        Question("Are penalty shootouts used to decide some tied matches?", AnswerType.YES),
        Question("Is a yellow card in football a warning from the referee?", AnswerType.YES),
        Question("Is a football field typically rectangular in shape?", AnswerType.YES),
        Question("Do players wear cleats to get better traction on the field?", AnswerType.YES),
        Question("Is football also known as soccer in some countries?", AnswerType.YES),
        Question(
            "Does FIFA stand for Fédération Internationale de Football Association?",
            AnswerType.YES
        ),
        Question(
            "Is a nutmeg in football when the ball is passed through an opponent's legs?",
            AnswerType.YES
        ),
        Question("Is the Premier League the top-tier football league in England?", AnswerType.YES),
        Question("Can football be traced back to ancient civilizations?", AnswerType.YES),
        Question("Is a backheel a type of skill move in football?", AnswerType.YES),
        Question(
            "Are football matches played in two halves with a halftime break?",
            AnswerType.YES
        ),
        Question("Is the World Cup trophy called the FIFA World Cup Trophy?", AnswerType.YES),
        Question("Is a penalty kick taken from the penalty spot?", AnswerType.YES),
        Question("Are football matches played underwater?", AnswerType.NO),
        Question("Do players use their heads to play football?", AnswerType.NO),
        Question("Is basketball the same as football?", AnswerType.NO),
        Question("Do football players use a racket to hit the ball?", AnswerType.NO)
    )

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    val coins = MutableLiveData(5000)
    private var currentBet = 0
    private var isBetPlaced = false
    private var answeredQuestionsCount = 0
    private var correctlyAnsweredCount = 0
    private var correctAnswers10to14 = 0
    private val prizeMultiplier = 2.0

    private fun incrementCorrectlyAnsweredCount() {
        correctlyAnsweredCount++
    }

    fun incrementAnsweredQuestionsCount() {
        answeredQuestionsCount++
    }

    fun getAnsweredQuestionsCount(): Int {
        return answeredQuestionsCount
    }

    fun shuffleQuestions() {
        questions.shuffled()
        currentQuestionIndex = 0 // Сброс текущего индекса вопроса после перемешивания
    }

    fun doubleBetAndAddToCoins() {
        val prizeMultiplier = if (correctAnswers10to14 in 10..14) 1.5 else 2.0
        coins.value = coins.value?.plus((currentBet * prizeMultiplier).toInt())
    }

    fun getCurrentQuestion(): String {
        return questions[currentQuestionIndex].text
    }

    fun answerQuestion(answer: AnswerType) {
        if (answer == questions[currentQuestionIndex].answerType) {
            correctAnswers++
            incrementCorrectlyAnsweredCount() // Увеличиваем счетчик правильных ответов

            if (correctAnswers in 10..14) {
                correctAnswers10to14++
            }
        }
        currentQuestionIndex++
        incrementAnsweredQuestionsCount()
    }

    fun getCorrectlyAnsweredCount(): Int {
        return correctlyAnsweredCount
    }

    fun getCoins(): Int? {
        return coins.value
    }

    fun getCurrentBet(): Int {
        return currentBet
    }

    fun setPlayerBet(bet: Int): Boolean {
        if (!isBetPlaced && bet <= coins.value!!) {
            currentBet = bet
            isBetPlaced = true
            return true
        }
        return false
    }

    fun isBetPlaced(): Boolean {
        return isBetPlaced
    }

    fun getTotalQuestionsCount(): Int {
        return questions.size - 1
    }

    fun restartGame() {
        currentQuestionIndex = 0
        correctAnswers = 0
        correctAnswers10to14 = 0
        currentBet = 0
        isBetPlaced = false
        answeredQuestionsCount = 0
        correctlyAnsweredCount = 0 // Обнуляем счетчик правильных ответов при рестарте игры
    }


    fun resetCoinsTo5000() {
        coins.value = 5000
        currentQuestionIndex = 0
        correctAnswers10to14 = 0
        correctAnswers = 0
        currentBet = 0
        isBetPlaced = false
        answeredQuestionsCount = 0
        correctlyAnsweredCount = 0
    }

    fun isGameWon(): Boolean {
        val correctlyAnswered = getCorrectlyAnsweredCount()
        return correctlyAnswered in 10..15
    }
}
