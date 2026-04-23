const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp();

const db = admin.firestore();
const messaging = admin.messaging();

/**
 * Sends a notification when a new flashcard is created.
 * Notifies users subscribed to the "new_content" topic.
 */
exports.onFlashcardCreated = functions.firestore
  .document('flashcards/{flashcardId}')
  .onCreate(async (snap, context) => {
    const flashcard = snap.data();
    const flashcardId = context.params.flashcardId;

    console.log(`📚 New flashcard created: ${flashcard.term}`);

    // Prepare notification payload
    const payload = {
      topic: 'new_content',
      notification: {
        title: '🆕 بطاقة جديدة متاحة',
        body: `${flashcard.term} في فئة ${flashcard.category}`,
        icon: 'ic_notification',
        click_action: 'FLUTTER_NOTIFICATION_CLICK'
      },
      data: {
        type: 'new_flashcard',
        flashcard_id: flashcardId,
        content_title: flashcard.term,
        category: flashcard.category,
        target_screen: 'flashcards'
      }
    };

    try {
      const response = await messaging.send(payload);
      console.log('✅ Flashcard notification sent:', response);
      
      // Log notification for analytics
      await db.collection('notification_logs').add({
        type: 'new_flashcard',
        flashcard_id: flashcardId,
        sent_at: admin.firestore.FieldValue.serverTimestamp(),
        response: response
      });
      
    } catch (error) {
      console.error('❌ Failed to send flashcard notification:', error);
    }
  });

/**
 * Sends a notification when a new question is posted.
 */
exports.onQuestionCreated = functions.firestore
  .document('questions/{questionId}')
  .onCreate(async (snap, context) => {
    const question = snap.data();
    const questionId = context.params.questionId;

    console.log(`❓ New question posted: ${question.question.substring(0, 50)}...`);

    const payload = {
      topic: 'new_content',
      notification: {
        title: '❓ سؤال جديد',
        body: question.question.length > 100 
          ? question.question.substring(0, 100) + '...' 
          : question.question,
        icon: 'ic_notification'
      },
      data: {
        type: 'new_question',
        question_id: questionId,
        content_title: question.question,
        category: question.category,
        target_screen: 'qa'
      }
    };

    try {
      const response = await messaging.send(payload);
      console.log('✅ Question notification sent:', response);
      
      await db.collection('notification_logs').add({
        type: 'new_question',
        question_id: questionId,
        sent_at: admin.firestore.FieldValue.serverTimestamp(),
        response: response
      });
      
    } catch (error) {
      console.error('❌ Failed to send question notification:', error);
    }
  });

/**
 * Sends a notification when someone answers a user's question.
 */
exports.onAnswerCreated = functions.firestore
  .document('answers/{answerId}')
  .onCreate(async (snap, context) => {
    const answer = snap.data();
    const answerId = context.params.answerId;

    console.log(`💬 New answer posted for question: ${answer.questionId}`);

    try {
      // Get the original question to find the author
      const questionDoc = await db.collection('questions').doc(answer.questionId).get();
      if (!questionDoc.exists) {
        console.log('❌ Question not found for answer');
        return;
      }

      const question = questionDoc.data();
      const questionAuthor = question.contributor;

      // Don't notify if the answer author is the same as question author
      if (answer.contributor === questionAuthor) {
        console.log('🔄 Same user answered their own question, skipping notification');
        return;
      }

      // Get the question author's FCM token
      const tokenDoc = await db.collection('user_tokens').doc(questionAuthor).get();
      if (!tokenDoc.exists) {
        console.log('❌ No FCM token found for question author');
        return;
      }

      const fcmToken = tokenDoc.data().fcmToken;

      // Get answer author's display name
      const answerAuthorDoc = await db.collection('users').doc(answer.contributor).get();
      const answerAuthorName = answerAuthorDoc.exists 
        ? answerAuthorDoc.data().displayName 
        : 'أحد المستخدمين';

      const payload = {
        token: fcmToken,
        notification: {
          title: '💬 إجابة جديدة على سؤالك!',
          body: `${answerAuthorName} أجاب على: ${question.question.substring(0, 50)}...`,
          icon: 'ic_notification'
        },
        data: {
          type: 'answer_received',
          answer_id: answerId,
          question_id: answer.questionId,
          question_title: question.question,
          answer_author: answerAuthorName,
          target_screen: 'qa'
        }
      };

      const response = await messaging.send(payload);
      console.log('✅ Answer notification sent:', response);
      
      await db.collection('notification_logs').add({
        type: 'answer_received',
        answer_id: answerId,
        question_id: answer.questionId,
        recipient: questionAuthor,
        sent_at: admin.firestore.FieldValue.serverTimestamp(),
        response: response
      });

    } catch (error) {
      console.error('❌ Failed to send answer notification:', error);
    }
  });

/**
 * Sends daily study reminders to users who have enabled them.
 * Scheduled to run every day at 7 PM (19:00) UTC.
 */
exports.sendDailyStudyReminders = functions.pubsub
  .schedule('0 19 * * *')
  .timeZone('Asia/Riyadh') // Adjust timezone as needed
  .onRun(async (context) => {
    console.log('📅 Running daily study reminders...');

    try {
      // Get all users with study reminders enabled
      const settingsSnapshot = await db.collection('notification_settings')
        .where('studyReminders', '==', true)
        .get();

      if (settingsSnapshot.empty) {
        console.log('📭 No users have study reminders enabled');
        return;
      }

      const batch = db.batch();
      const notifications = [];

      for (const doc of settingsSnapshot.docs) {
        const userId = doc.id;
        const settings = doc.data();
        
        // Get user's FCM token
        const tokenDoc = await db.collection('user_tokens').doc(userId).get();
        if (!tokenDoc.exists) continue;
        
        const fcmToken = tokenDoc.data().fcmToken;
        
        // Here you would typically check how many cards are due for review
        // For now, we'll send a generic reminder
        const dueCount = Math.floor(Math.random() * 20) + 1; // Placeholder
        
        const payload = {
          token: fcmToken,
          notification: {
            title: '⏰ وقت المراجعة!',
            body: `لديك ${dueCount} بطاقة جاهزة للمراجعة اليوم`,
            icon: 'ic_notification'
          },
          data: {
            type: 'study_reminder',
            due_count: dueCount.toString(),
            target_screen: 'study'
          }
        };

        notifications.push(messaging.send(payload));
        
        // Log the reminder
        const logRef = db.collection('notification_logs').doc();
        batch.set(logRef, {
          type: 'study_reminder',
          recipient: userId,
          due_count: dueCount,
          sent_at: admin.firestore.FieldValue.serverTimestamp()
        });
      }

      // Send all notifications
      const results = await Promise.allSettled(notifications);
      const successCount = results.filter(r => r.status === 'fulfilled').length;
      const failureCount = results.filter(r => r.status === 'rejected').length;

      console.log(`✅ Study reminders sent: ${successCount} success, ${failureCount} failed`);
      
      // Commit logs
      await batch.commit();

    } catch (error) {
      console.error('❌ Failed to send daily study reminders:', error);
    }
  });

/**
 * Sends achievement notifications when users reach milestones.
 */
exports.onAchievementUnlocked = functions.firestore
  .document('users/{userId}')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    const userId = context.params.userId;

    // Check for point milestones
    const pointMilestones = [100, 500, 1000, 2500, 5000, 10000];
    const beforePoints = before.points || 0;
    const afterPoints = after.points || 0;

    const newMilestone = pointMilestones.find(
      milestone => beforePoints < milestone && afterPoints >= milestone
    );

    if (!newMilestone) return;

    console.log(`🏆 User ${userId} reached ${newMilestone} points!`);

    try {
      // Get user's FCM token
      const tokenDoc = await db.collection('user_tokens').doc(userId).get();
      if (!tokenDoc.exists) return;

      const fcmToken = tokenDoc.data().fcmToken;
      const achievementName = getAchievementName(newMilestone);

      const payload = {
        token: fcmToken,
        notification: {
          title: '🏆 إنجاز جديد مفتوح!',
          body: `تهانينا! حصلت على ${achievementName}`,
          icon: 'ic_notification'
        },
        data: {
          type: 'achievement_unlocked',
          achievement_name: achievementName,
          points: newMilestone.toString(),
          target_screen: 'profile'
        }
      };

      const response = await messaging.send(payload);
      console.log('✅ Achievement notification sent:', response);

      await db.collection('notification_logs').add({
        type: 'achievement_unlocked',
        recipient: userId,
        achievement: achievementName,
        points: newMilestone,
        sent_at: admin.firestore.FieldValue.serverTimestamp(),
        response: response
      });

    } catch (error) {
      console.error('❌ Failed to send achievement notification:', error);
    }
  });

/**
 * HTTP function to send custom notifications (for admin use).
 */
exports.sendCustomNotification = functions.https.onCall(async (data, context) => {
  // Verify admin authentication
  if (!context.auth || !context.auth.token.admin) {
    throw new functions.https.HttpsError('permission-denied', 'Admin access required');
  }

  const { title, body, topic, targetScreen, customData } = data;

  try {
    const payload = {
      topic: topic || 'general',
      notification: {
        title: title,
        body: body,
        icon: 'ic_notification'
      },
      data: {
        type: 'custom',
        target_screen: targetScreen || 'home',
        ...customData
      }
    };

    const response = await messaging.send(payload);
    console.log('✅ Custom notification sent:', response);

    return { success: true, messageId: response };
  } catch (error) {
    console.error('❌ Failed to send custom notification:', error);
    throw new functions.https.HttpsError('internal', 'Failed to send notification');
  }
});

/**
 * Helper function to get achievement names in Arabic.
 */
function getAchievementName(points) {
  const achievements = {
    100: 'المبتدئ المتحمس',
    500: 'الطالب المجتهد',
    1000: 'المساهم النشط',
    2500: 'الخبير المتميز',
    5000: 'المعلم المحترف',
    10000: 'أسطورة التعلم'
  };
  return achievements[points] || 'إنجاز خاص';
}

/**
 * Automatically moderates content when it's created.
 * This function runs server-side moderation checks.
 */
exports.moderateContent = functions.firestore
  .document('{collection}/{documentId}')
  .onCreate(async (snap, context) => {
    const collection = context.params.collection;
    const documentId = context.params.documentId;
    
    // Only moderate specific collections
    if (!['flashcards', 'questions', 'answers'].includes(collection)) {
      return;
    }

    const data = snap.data();
    console.log(`🛡️ Moderating ${collection} content: ${documentId}`);

    try {
      // Get content to moderate
      let contentToModerate = '';
      switch (collection) {
        case 'flashcards':
          contentToModerate = `${data.term} ${data.definition}`;
          break;
        case 'questions':
          contentToModerate = data.question;
          break;
        case 'answers':
          contentToModerate = data.content;
          break;
      }

      // Basic server-side moderation checks
      const moderationResult = await performServerModeration(contentToModerate, data.contributor);
      
      // Update document with moderation result
      await snap.ref.update({
        serverModerationScore: moderationResult.score,
        serverModerationFlags: moderationResult.flags,
        lastModerated: admin.firestore.FieldValue.serverTimestamp()
      });

      // If high risk, add to manual review queue
      if (moderationResult.score > 0.7) {
        await db.collection('pending_review').add({
          contentId: documentId,
          contentType: collection.slice(0, -1), // Remove 's' from collection name
          content: contentToModerate,
          authorId: data.contributor,
          moderationScore: moderationResult.score,
          flags: moderationResult.flags,
          priority: calculatePriority(moderationResult.score, moderationResult.flags),
          status: 'PENDING',
          createdAt: admin.firestore.FieldValue.serverTimestamp()
        });
        
        console.log(`📋 Content flagged for manual review: ${documentId}`);
      }

    } catch (error) {
      console.error(`❌ Content moderation failed for ${documentId}:`, error);
    }
  });

/**
 * Performs server-side content moderation.
 */
async function performServerModeration(content, authorId) {
  let score = 0;
  const flags = [];

  // Length checks
  if (content.length < 3) {
    score += 0.5;
    flags.push('TOO_SHORT');
  }
  if (content.length > 2000) {
    score += 0.3;
    flags.push('EXCESSIVE_LENGTH');
  }

  // Spam detection
  const spamKeywords = [
    'اضغط هنا', 'رابط', 'تحميل مجاني', 'عرض خاص',
    'click here', 'free download', 'special offer', 'buy now'
  ];
  
  const contentLower = content.toLowerCase();
  for (const keyword of spamKeywords) {
    if (contentLower.includes(keyword.toLowerCase())) {
      score += 0.6;
      flags.push('SPAM_CONTENT');
      break;
    }
  }

  // Repetitive content
  const words = content.split(/\s+/);
  const wordCounts = {};
  words.forEach(word => {
    wordCounts[word.toLowerCase()] = (wordCounts[word.toLowerCase()] || 0) + 1;
  });
  
  const maxWordCount = Math.max(...Object.values(wordCounts));
  if (maxWordCount > words.length * 0.3) {
    score += 0.4;
    flags.push('REPETITIVE_CONTENT');
  }

  // Excessive caps
  const capsRatio = (content.match(/[A-Z]/g) || []).length / content.length;
  if (capsRatio > 0.5) {
    score += 0.3;
    flags.push('EXCESSIVE_CAPS');
  }

  // User reputation adjustment
  try {
    const userDoc = await db.collection('users').doc(authorId).get();
    if (userDoc.exists) {
      const userData = userDoc.data();
      const points = userData.points || 0;
      const contributionCount = userData.contributionCount || 0;
      
      // Reduce score for trusted users
      if (points > 1000 && contributionCount > 50) {
        score *= 0.7; // 30% reduction for trusted users
      } else if (points < 100 && contributionCount < 5) {
        score *= 1.2; // 20% increase for new users
        flags.push('NEW_USER');
      }
    }
  } catch (error) {
    console.warn('Failed to get user reputation:', error);
  }

  return {
    score: Math.min(score, 1.0), // Cap at 1.0
    flags: flags
  };
}

/**
 * Calculates review priority based on moderation score and flags.
 */
function calculatePriority(score, flags) {
  let priority = Math.floor(score * 10); // Base priority 0-10
  
  // Increase priority for specific flags
  if (flags.includes('SPAM_CONTENT')) priority += 3;
  if (flags.includes('BLACKLISTED_CONTENT')) priority += 5;
  if (flags.includes('NEW_USER')) priority += 1;
  
  return Math.min(priority, 10); // Cap at 10
}

/**
 * Processes manual review decisions.
 */
exports.processReviewDecision = functions.firestore
  .document('pending_review/{reviewId}')
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();
    
    // Only process when status changes to REVIEWED
    if (before.status !== 'REVIEWED' && after.status === 'REVIEWED') {
      const reviewId = context.params.reviewId;
      const decision = after.reviewDecision;
      const contentId = after.contentId;
      const contentType = after.contentType;
      
      console.log(`📝 Processing review decision: ${decision} for ${contentId}`);
      
      try {
        // Update the original content based on decision
        const collectionName = contentType + 's'; // Add 's' for collection name
        const contentRef = db.collection(collectionName).doc(contentId);
        
        switch (decision) {
          case 'APPROVE':
            await contentRef.update({
              reviewStatus: 'APPROVED',
              reviewedAt: admin.firestore.FieldValue.serverTimestamp(),
              reviewerId: after.reviewerId
            });
            break;
            
          case 'REJECT':
            // Mark as rejected but keep for audit
            await contentRef.update({
              reviewStatus: 'REJECTED',
              isVisible: false,
              reviewedAt: admin.firestore.FieldValue.serverTimestamp(),
              reviewerId: after.reviewerId
            });
            
            // Notify content author
            await notifyContentAuthor(after.authorId, 'CONTENT_REJECTED', {
              contentType: contentType,
              reason: after.reviewerNotes || 'Content did not meet community guidelines'
            });
            break;
            
          case 'REQUEST_CHANGES':
            await contentRef.update({
              reviewStatus: 'CHANGES_REQUESTED',
              reviewedAt: admin.firestore.FieldValue.serverTimestamp(),
              reviewerId: after.reviewerId,
              requestedChanges: after.reviewerNotes
            });
            
            // Notify content author
            await notifyContentAuthor(after.authorId, 'CHANGES_REQUESTED', {
              contentType: contentType,
              changes: after.reviewerNotes
            });
            break;
        }
        
        // Log the moderation action
        await db.collection('moderation_actions').add({
          reviewId: reviewId,
          contentId: contentId,
          contentType: contentType,
          decision: decision,
          reviewerId: after.reviewerId,
          notes: after.reviewerNotes,
          timestamp: admin.firestore.FieldValue.serverTimestamp()
        });
        
        console.log(`✅ Review decision processed: ${decision}`);
        
      } catch (error) {
        console.error(`❌ Failed to process review decision:`, error);
      }
    }
  });

/**
 * Notifies content authors about moderation decisions.
 */
async function notifyContentAuthor(authorId, type, data) {
  try {
    // Get author's FCM token
    const tokenDoc = await db.collection('user_tokens').doc(authorId).get();
    if (!tokenDoc.exists) return;
    
    const fcmToken = tokenDoc.data().fcmToken;
    
    let title, body;
    switch (type) {
      case 'CONTENT_REJECTED':
        title = '❌ تم رفض المحتوى';
        body = `تم رفض ${data.contentType} الخاص بك: ${data.reason}`;
        break;
      case 'CHANGES_REQUESTED':
        title = '✏️ مطلوب تعديلات';
        body = `يرجى تعديل ${data.contentType}: ${data.changes}`;
        break;
    }
    
    const payload = {
      token: fcmToken,
      notification: { title, body },
      data: {
        type: type,
        contentType: data.contentType,
        target_screen: 'profile'
      }
    };
    
    await messaging.send(payload);
    console.log(`📨 Moderation notification sent to ${authorId}`);
    
  } catch (error) {
    console.warn(`Failed to notify author ${authorId}:`, error);
  }
}